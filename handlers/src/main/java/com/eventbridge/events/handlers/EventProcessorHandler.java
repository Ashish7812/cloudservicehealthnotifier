package com.eventbridge.events.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.StringUtils;
import com.eventbridge.events.deliverychannel.ChannelInfo;
import com.eventbridge.events.deliverychannel.ChannelRepository;
import com.eventbridge.events.deliverychannel.EventsRepository;
import com.eventbridge.events.exception.EventsProcessorException;
import com.eventbridge.events.exception.ValidationException;
import com.eventbridge.events.guice.NotificationHandlerGuiceModule;
import com.eventbridge.events.model.DispatcherInfo;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.EventResponse;
import com.eventbridge.events.model.EventSource;
import com.eventbridge.events.model.ProcessedEventDetails;
import com.eventbridge.events.model.QueryEventRequest;
import com.eventbridge.events.processors.EventsProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

/**
 * This is the entry point to the event bridge notification.
 * 
 * @author sahoa
 *
 */
public class EventProcessorHandler extends NotificationHandlerGuiceModule
		implements RequestStreamHandler {
	
	private static final Logger LOG = LogManager.getLogger(EventProcessorHandler.class);
	
	private EventsRepository eventsRepository;
	private ChannelRepository channelRepository;

	private EventsProcessor processor;
	private Event event;

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode payload = mapper.readTree(input);

		String eventSource = payload.get("source").asText();
		
		String eventId = payload.get("id").asText();
		
		boolean isCloudTrailEvent = payload.get("detail-type").asText().contains("CloudTrail");
		
		String eventName = Strings.EMPTY;
		
		String eventData = mapper.writeValueAsString(payload);
		
		if (EventSource.AWS_HEALTH.equals(eventSource)) {
			JsonNode extractEventName = payload.get("detail").get("eventTypeCode");
			eventName = extractEventName == null ? "" : extractEventName.asText();
		} else {
			JsonNode extractEventName = payload.get("detail").get("eventName");
			eventName = extractEventName == null ? "" : extractEventName.asText();
		}
		
		QueryEventRequest queryRequest = new QueryEventRequest(eventSource, eventName, eventId);
		
		try {
			validateRequest(queryRequest);
			
			super.injectMembers(eventSource, eventName, eventData, isCloudTrailEvent);
			
			if (!eventsRepository.isExistingEvent(queryRequest)) {
				ProcessedEventDetails eventInfo = processor.processEvent(event);
				
				eventsRepository.putEvent(queryRequest, eventInfo.getEvent());
				updateEventDispatcherDetails(queryRequest, eventInfo);
			} else {
				LOG.info("This event is already processed hence skipping this event from being processed.");
			}
			
			writeOutput(output, eventSource, eventId, eventName);
			
		} catch (EventsProcessorException e) {
			LOG.error(e.getMessage(), e);
			
			handleError(eventName, queryRequest, e.getMessage());
			writeOutput(output, eventSource, eventId, eventName);
			
		} catch (Exception e) {
			
			String errorMessage = String.format("An error occured. Cause: %s", e.getMessage());
			LOG.error(errorMessage, e);
			
			throw new ValidationException(errorMessage, e);
		}
	}

	private void writeOutput(OutputStream output, String eventSource, String eventId, String eventName)
			throws IOException {
		EventResponse eventResponse = new EventResponse(eventSource, eventName, eventId);
		
		output.write(eventResponse.toString().getBytes(StandardCharsets.UTF_8));
	}

	private void handleError(String eventName, QueryEventRequest queryRequest, String errorMessage) {		
		ProcessedEventDetails eventInfo = new ProcessedEventDetails(eventName, errorMessage, "", event);
		eventsRepository.putEvent(queryRequest, eventInfo.getEvent());
		updateEventDispatcherDetails(queryRequest, eventInfo);
		eventsRepository.putErrorMessage(queryRequest, errorMessage);
	}
	
	private void validateRequest(QueryEventRequest queryRequest) {
		if (!StringUtils.hasValue(queryRequest.getEventId()) ||
			!StringUtils.hasValue(queryRequest.getEventSource()) ||
			!StringUtils.hasValue(queryRequest.getEventName())) {
			
			throw new ValidationException("One of the field eventId, eventSource or eventName is missing.");
		}
	}
	
	public void updateEventDispatcherDetails(QueryEventRequest queryRequest, ProcessedEventDetails eventDetails) {
		if (!eventDetails.getEventCode().isEmpty()) {
			List<ChannelInfo> channelDetails = channelRepository.readChannelDetails(eventDetails.getEventCode());

			DispatcherInfo dispatherInfo = new DispatcherInfo(eventDetails.getMessage(), channelDetails);
			
			eventsRepository.putDispatcherInfo(queryRequest, dispatherInfo);
		}
	}
	
	@Inject
	public void setProcessor(EventsProcessor processor) {
		this.processor = processor;
	}

	@Inject
	public void setEvent(Event event) {
		this.event = event;
	}
	
	@Inject
	public void setEventsRepository(EventsRepository eventsRepository) {
		this.eventsRepository = eventsRepository;
	}
	
	@Inject
	public void setChannelRepository(ChannelRepository channelRepository) {
		this.channelRepository = channelRepository;
	}
}

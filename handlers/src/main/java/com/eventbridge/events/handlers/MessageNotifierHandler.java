package com.eventbridge.events.handlers;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.eventbridge.events.deliverychannel.DeliveryChannel;
import com.eventbridge.events.deliverychannel.EventsRepository;
import com.eventbridge.events.exception.MessageNotifierException;
import com.eventbridge.events.guice.NotificationHandlerGuiceModule;
import com.eventbridge.events.model.EventInfo;
import com.eventbridge.events.model.EventResponse;
import com.eventbridge.events.model.FormatMessageRequest;
import com.eventbridge.events.model.QueryEventRequest;
import com.google.inject.Inject;

public class MessageNotifierHandler extends NotificationHandlerGuiceModule
		implements RequestHandler<EventResponse, String> {
	
	private static final Logger LOG = LogManager.getLogger(MessageNotifierHandler.class);
	
	private Map<String, DeliveryChannel> channelTypeToDeliveryChannel;
	private EventsRepository eventsRepository;

	@Override
	public String handleRequest(EventResponse input, Context context) {
		LOG.info("The input request is {}", input);
		
		super.injectNotifierMembers();
		
		String notifiedParties = Strings.EMPTY;
		
		QueryEventRequest queryRequest = new QueryEventRequest(input.getEventSource(), input.getEventName(),
				input.getEventId());
		
		try {
			EventInfo eventInfo = eventsRepository.readEvent(queryRequest);
			
			String eventDescription = eventsRepository.readEventDescription(queryRequest);
			
			LOG.info("Received the eveint info from DB {}", eventInfo);
			
			notifiedParties = eventInfo.getDispatherInfo().toString();
			
			String errorMessage = eventInfo.getErrorMessage() == null ? "" : eventInfo.getErrorMessage();
			
			eventInfo.getDispatherInfo().getChannelInfos().forEach(channel -> {
				DeliveryChannel deliveryChannel = channelTypeToDeliveryChannel.get(channel.getChannelType());
				
				FormatMessageRequest messageRequest = new FormatMessageRequest();
				
				messageRequest.setMessage(eventInfo.getDispatherInfo().getMessage());
				messageRequest.setErrorMessage(errorMessage);
				messageRequest.setEvent(eventInfo.getEvent());
				messageRequest.setEventName(input.getEventName());
				messageRequest.setEventDescription(eventDescription);
				
				LOG.info("The formatted message request is {}", messageRequest);
				
				String formattedMessage = deliveryChannel.formatMessage(messageRequest);
				
				deliveryChannel.sendMessage(formattedMessage, channel.getChannelIds());
			});
		} catch (Exception e) {
			String errorMessage = String.format(
					"The event can not sent to the interested parties, an error occured. Cause: %s", e.getMessage());
			
			LOG.error(errorMessage, e);
			
			logErrorAndThrowException(queryRequest, errorMessage);
		}
		
		return "The event has been notified to " + notifiedParties;
	}
	
	private void logErrorAndThrowException(QueryEventRequest queryRequest, String errorMessage) {
		String existingErrorMessage = eventsRepository.readErrorMessage(queryRequest);
		
		String updatedErrorMessage = existingErrorMessage == null ? "".concat(errorMessage)
				: existingErrorMessage.concat(errorMessage);
		
		eventsRepository.putErrorMessage(queryRequest, updatedErrorMessage);

		throw new MessageNotifierException(errorMessage);
	}
	
	@Inject
	public void setEventsRepository(EventsRepository eventsRepository) {
		this.eventsRepository = eventsRepository;
	}
	
	@Inject
	public void setChannelTypeToDeliveryChannel(Map<String, DeliveryChannel> channelTypeToDeliveryChannel) {
		this.channelTypeToDeliveryChannel = channelTypeToDeliveryChannel;
	}
}

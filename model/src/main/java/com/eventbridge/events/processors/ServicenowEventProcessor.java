package com.eventbridge.events.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eventbridge.events.clients.SlackClient;
import com.eventbridge.events.clients.EMailClient;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.EventSource;
import com.eventbridge.events.model.EventType;
import com.eventbridge.events.model.ProcessedEventDetails;
import com.eventbridge.events.model.ServicenowEvent;
import com.eventbridge.events.processors.annotation.EventProcessorMetadata;
import com.google.inject.Inject;

@EventProcessorMetadata(eventProcessorName = "ServicenowEventProcessor", 
						eventSource = EventSource.SERVICE_NOW, 
						eventType = EventType.NOW.CREATE_TICKET,
						eventDescription = "This class processes the events from the servienow application.")
public class ServicenowEventProcessor implements EventsProcessor {
	private static final Logger LOG = LogManager.getLogger(ServicenowEventProcessor.class);
	
	private SlackClient webexClient;
	private EMailClient sendMailClient;
	
	@Override
	public ProcessedEventDetails processEvent(Event event) {
		ServicenowEvent servicenowEvent = (ServicenowEvent) event;
		
		LOG.info("----------------- LOGGING SERVICENOW EVENT DATA ------------------");

		LOG.info("==================================================");

		LOG.info("Event name is " + servicenowEvent.getDetail().getEventName());

		servicenowEvent.getDetail().getRequestParameters().forEach((key, value) -> {
			LOG.info("Key: " + key);
			LOG.info("Value: " + value);
		});

		LOG.info("==================================================");

		webexClient.notifySlackChannel("The servicenow event is procesed successfully.",
				"ServicenowEventListeners");
		
		sendMailClient.sendMail("The servicenow event is procesed successfully.",
				"notifyadmins@iiit.in.bbsr");

		return new ProcessedEventDetails(servicenowEvent.getDetail().getEventName(), "", "", servicenowEvent);
	}
	
	@Inject
	public void setWebexClient(SlackClient webexClient) {
		this.webexClient = webexClient;
	}
	
	@Inject
	public void setSendMailClient(EMailClient sendMailClient) {
		this.sendMailClient = sendMailClient;
	}
}

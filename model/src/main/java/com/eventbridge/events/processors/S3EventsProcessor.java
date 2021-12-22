package com.eventbridge.events.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eventbridge.events.clients.EMailClient;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.EventSource;
import com.eventbridge.events.model.EventType;
import com.eventbridge.events.model.ProcessedEventDetails;
import com.eventbridge.events.model.CloudtrailEvent;
import com.eventbridge.events.processors.annotation.EventProcessorMetadata;
import com.google.inject.Inject;

@EventProcessorMetadata(eventProcessorName = "S3EventsProcessor", 
						eventSource = EventSource.AWS_S3,
						eventType = EventType.S3.PUT_OBJECT,
						eventDescription = "This class processes the events from the S3.")
public class S3EventsProcessor implements EventsProcessor {
	private static final Logger LOG = LogManager.getLogger(S3EventsProcessor.class);
	
	private EMailClient sendMailClient;

	@Override
	public ProcessedEventDetails processEvent(Event event) {
		CloudtrailEvent s3Event = (CloudtrailEvent) event;
		
		LOG.info("----------------- LOGGING S3 EVENT DATA ------------------");
		
		LOG.info("==================================================");
		
		LOG.info("Event name is " + s3Event.getDetail().getEventName());
		
		LOG.info("The event id is " + s3Event.getId());
		LOG.info("The event source is " + s3Event.getSource());
		LOG.info("The event is coming from account " + s3Event.getAccount());
		
		s3Event.getDetail().getRequestParameters().forEach((key, value) -> {
			LOG.info("Key: " + key);
			LOG.info("Value: " + value);
		});
		
		LOG.info("==================================================");
		
		sendMailClient.sendMail("The S3 event is processed successfully.",
				"notifyadmins@iiit.in.bbsr");

		return new ProcessedEventDetails(s3Event.getDetail().getEventName(), "", "", s3Event);
	}
	
	@Inject
	public void setSendMailClient(EMailClient sendMailClient) {
		this.sendMailClient = sendMailClient;
	}
}

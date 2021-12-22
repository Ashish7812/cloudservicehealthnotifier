package com.eventbridge.events.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eventbridge.events.enrichevent.EnrichEC2Event;
import com.eventbridge.events.exception.EventsProcessorException;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.EventSource;
import com.eventbridge.events.model.EventType;
import com.eventbridge.events.model.PersonalHealthBoardEvent;
import com.eventbridge.events.model.ProcessedEventDetails;
import com.eventbridge.events.processors.annotation.EventProcessorMetadata;
import com.google.inject.Inject;

@EventProcessorMetadata(
		eventProcessorName = "PersonalHealthBoardEC2EventProcessor", 
		eventSource = EventSource.AWS_HEALTH, 
		eventType = EventType.PHD_EC2.EC2_AUTO_RECOVERRY_SUCCESS,
		eventDescription = "This class processes the events from the personal health board application for EC2."
)
public class PersonalHealthBoardEC2EventProcessor implements EventsProcessor {
	private static final Logger LOG = LogManager.getLogger(PersonalHealthBoardEC2EventProcessor.class);
	
	private EnrichEC2Event enrichEC2Event;
	
	@Override
	public ProcessedEventDetails processEvent(Event event) {
		try {
			PersonalHealthBoardEvent healthEvent = (PersonalHealthBoardEvent) enrichEC2Event.enrichEvent(event);
			
			LOG.info("Enriched ec2 event is " + healthEvent);
			
			LOG.info("----------------- LOGGING PHD HEALTH EVENT DATA ------------------");
			
			LOG.info("==================================================");
			
			LOG.info("Event name is " + healthEvent.getDetail().getEventTypeCode());
			
			LOG.info("Event category is " + healthEvent.getDetail().getEventTypeCategory());
			
			LOG.info("The event id is " + healthEvent.getId());
			
			LOG.info("The event source is " + healthEvent.getSource());
			
			LOG.info("The event is coming from account " + healthEvent.getAccount());
			
			LOG.info("==================================================");
			
			return new ProcessedEventDetails(healthEvent.getDetail().getEventTypeCode(), "", "The personal event of ec2 is processed successfully.", healthEvent);
			
		} catch (Exception e) {
			String errorMessage = String.format("An error occured while processing the event. Cause: %s",
					e.getMessage());
			
			throw new EventsProcessorException(errorMessage, e);
		}
	}

	@Inject
	public void setEnrichEC2Event(EnrichEC2Event enrichEC2Event) {
		this.enrichEC2Event = enrichEC2Event;
	}
}

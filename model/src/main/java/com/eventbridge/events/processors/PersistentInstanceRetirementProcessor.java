package com.eventbridge.events.processors;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eventbridge.events.clients.EC2Client;
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
		eventProcessorName = "PersistentInstanceRetirementProcessor", 
		eventSource = EventSource.AWS_HEALTH, 
		eventType = EventType.PHD_EC2.EC2_PERSISTENT_INSTANCE_RETIREMENT_SCHEDULED,
		eventDescription = "This class accepts the event and stops the EC2 and starts it to complete the operation."
)
public class PersistentInstanceRetirementProcessor implements EventsProcessor {
	private static final Logger LOG = LogManager.getLogger(PersistentInstanceRetirementProcessor.class);
	
	private EnrichEC2Event enrichEC2Event;
	private EC2Client client;

	@Override
	public ProcessedEventDetails processEvent(Event event) {
		try {
			PersonalHealthBoardEvent healthEvent = (PersonalHealthBoardEvent) enrichEC2Event.enrichEvent(event);
			
			LOG.info("Enriched ec2 event is " + healthEvent);
			
			healthEvent.getResources().forEach(instance -> {
				if (instance.startsWith("i-")) {
					client.stopInstance(instance);
					sleep(5L);
					client.startInstance(instance);
				}
			});
			
			return new ProcessedEventDetails(healthEvent.getDetail().getEventTypeCode(), "",
					"The ec2 is stopped and started successfully.", healthEvent);

		} catch (Exception e) {
			String errorMessage = String.format("An error occured while processing the event. Cause: %s",
					e.getMessage());
			
			throw new EventsProcessorException(errorMessage, e);
		}
	}

	private void sleep(long timeToSleepInSeconds) {
		TimeUnit time = TimeUnit.SECONDS;
		
		try {
			time.sleep(timeToSleepInSeconds);
		} catch (InterruptedException e) {
			LOG.error(e);
		}
	}
	
	@Inject
	public void setEnrichEC2Event(EnrichEC2Event enrichEC2Event) {
		this.enrichEC2Event = enrichEC2Event;
	}
	
	@Inject
	public void setClient(EC2Client client) {
		this.client = client;
	}
}

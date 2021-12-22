package com.eventbridge.events.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.util.json.Jackson;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.ProcessedEventDetails;

public class DefaultEventProcessor implements EventsProcessor {
	private static final Logger LOG = LogManager.getLogger(DefaultEventProcessor.class);
	
	@Override
	public ProcessedEventDetails processEvent(Event event) {
		LOG.info("There is no implementation class found for the current event source. " + event.getSource());
		
		LOG.info("This event is being skipped. " + Jackson.toJsonPrettyString(event));
		
		return new ProcessedEventDetails("", "", "This event is skipped from processing.", event);
	}
}

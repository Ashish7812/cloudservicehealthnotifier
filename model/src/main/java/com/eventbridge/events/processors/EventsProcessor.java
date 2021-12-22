package com.eventbridge.events.processors;

import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.ProcessedEventDetails;

public interface EventsProcessor {
	public ProcessedEventDetails processEvent(Event event);
}	

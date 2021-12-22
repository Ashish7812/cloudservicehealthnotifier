package com.eventbridge.events.guice;

import com.eventbridge.events.exception.UnknownEventException;
import com.eventbridge.events.model.CloudtrailEvent;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.EventSource;
import com.eventbridge.events.model.PersonalHealthBoardEvent;
import com.eventbridge.events.processors.EventsProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;

public class EventsProcessorGuiceModule extends BaseGuiceModule {
	private static final String EVENTS_PROCESSOR_PACKAGE = "com.eventbridge.events.processors";
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private String eventSource;
	private String eventName;
	private String eventPayload;
	private boolean isCloudTrailEvent;

	public EventsProcessorGuiceModule(String eventSource, String eventName, String eventPayload,
			boolean isCloudTrailEvent) {
		
		this.eventSource = eventSource;
		this.eventName = eventName;
		this.eventPayload = eventPayload;
		this.isCloudTrailEvent = isCloudTrailEvent;
	}
	
	@SuppressWarnings({ "hiding" })
	public <EventsProcessor> EventsProcessor provideEventProcessorInstance(Class<EventsProcessor> clazz) {
		return super.provideEventProcessorInstance(clazz, EVENTS_PROCESSOR_PACKAGE, eventSource, eventName);
	}
	
	@SuppressWarnings({ "hiding", "unchecked" })
	public <Event> Event provideEventInstance(Class<Event> clazz) {
		Class<? extends Event> eventsClass = null;
		
		if (isCloudTrailEvent) {
			eventsClass = (Class<? extends Event>) CloudtrailEvent.class;
		} 
		
		if (eventSource.equals(EventSource.AWS_HEALTH)) {
			eventsClass = (Class<? extends Event>) PersonalHealthBoardEvent.class;
		}

		if (eventsClass != null) {
			try {
				return mapper.readValue(eventPayload, eventsClass);
			} catch (Exception e) {
				throw new UnknownEventException("The event class could not be found.", e);
			}
			
		} else {
			throw new UnknownEventException("The event class could not be found.");
		}
	}
	
	@Provides
	public EventsProcessor provideEventProcessor() {
		return provideEventProcessorInstance(EventsProcessor.class);
	}
	
	@Provides
	public Event provideParsedEvent() {
		return provideEventInstance(Event.class);
	}
}

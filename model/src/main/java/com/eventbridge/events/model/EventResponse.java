package com.eventbridge.events.model;

import com.amazonaws.util.json.Jackson;

public class EventResponse {
	private String eventSource;
	private String eventName;
	private String eventId;
	
	public EventResponse() {}
	
	public EventResponse(String eventSource,
			String eventName,
			String eventId) {
		
		this.eventSource = eventSource;
		this.eventName = eventName;
		this.eventId = eventId;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	@Override
	public String toString() {
		return Jackson.toJsonString(this);
	}
}
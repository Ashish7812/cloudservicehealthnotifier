package com.eventbridge.events.model;

import org.apache.logging.log4j.util.Strings;

import com.amazonaws.util.json.Jackson;

public class QueryEventRequest {
	private String eventSource;
	private String eventName;
	private String eventId;
	
	public QueryEventRequest() {}
	
	public QueryEventRequest(String eventSource,
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
	
	public String getSortKey() {
		if (getEventName() != null && getEventId() != null) {
			return getEventName() + "_" + getEventId();
		} else {
			return Strings.EMPTY;
		}
	}
	
	@Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}

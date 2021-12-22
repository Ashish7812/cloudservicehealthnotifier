package com.eventbridge.events.model;

import java.util.Collections;

import org.apache.logging.log4j.util.Strings;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventInfo {
	@JsonProperty("EventSource")
	private String eventSource;

	@JsonProperty("EventID")
	private String eventId;
	
	@JsonProperty("ErrorMessage")
	private String errorMessage;
	
	@JsonProperty("EventPayload")
	private Event event;
	
	@JsonProperty("DispatcherInfo")
	private DispatcherInfo dispatherInfo;
	
	public EventInfo() {}
	
	public EventInfo(String eventSource, 
			String eventId, 
			String errorMessage, 
			Event event,
			DispatcherInfo dispatherInfo) {
		
		this.eventSource = eventSource;
		this.eventId = eventId;
		this.errorMessage = errorMessage;
		this.event = event;
		this.dispatherInfo = dispatherInfo;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}
	
	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public DispatcherInfo getDispatherInfo() {
		return dispatherInfo == null ? new DispatcherInfo(Strings.EMPTY, Collections.emptyList()) : dispatherInfo;
	}

	public void setDispatherInfo(DispatcherInfo dispatherInfo) {
		this.dispatherInfo = dispatherInfo;
	}
	
	@Override
	public String toString() {
		return Jackson.toJsonString(this);
	}
}

package com.eventbridge.events.model;

import com.amazonaws.util.json.Jackson;

public class ProcessedEventDetails {
	private String eventCode;
	private String errorMessage;
	private String message;
	private Event event;
	
	public ProcessedEventDetails() {}
	
	public ProcessedEventDetails(String eventCode,
			String errorMessage,
			String message,
			Event event) {
		
		this.eventCode = eventCode;
		this.message = message;
		this.errorMessage = errorMessage;
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}

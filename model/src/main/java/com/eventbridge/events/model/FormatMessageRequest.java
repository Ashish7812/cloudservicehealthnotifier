package com.eventbridge.events.model;

import com.amazonaws.util.json.Jackson;

public class FormatMessageRequest {
	private String message;
	private String errorMessage;
	private String eventDescription;
	private String eventName;
	private Event event;
	
	public FormatMessageRequest() {}
	
	public FormatMessageRequest(String message,
			String errorMessage,
			String eventDescription,
			String eventName,
			Event event) {
		
		this.message = message;
		this.errorMessage = errorMessage;
		this.eventDescription = eventDescription;
		this.eventName = eventName;
		this.event = event;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	@Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}

package com.eventbridge.events.exception;

public class EventsProcessorException extends RuntimeException {
	private static final long serialVersionUID = -6230209086460532012L;

	public EventsProcessorException(String errorMessage) {
		super(errorMessage);
	}
	
	public EventsProcessorException(String errorMessage, Throwable cause) {
		super(errorMessage,cause);
	}
	
	public EventsProcessorException(Throwable cause) {
		super(cause);
	}
}

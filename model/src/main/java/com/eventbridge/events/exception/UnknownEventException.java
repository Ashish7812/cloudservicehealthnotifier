package com.eventbridge.events.exception;

public class UnknownEventException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnknownEventException(String errorMessage) {
		super(errorMessage);
	}
	
	public UnknownEventException(String errorMessage, Throwable cause) {
		super(errorMessage,cause);
	}
	
	public UnknownEventException(Throwable cause) {
		super(cause);
	}
}

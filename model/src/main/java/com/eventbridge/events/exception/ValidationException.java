package com.eventbridge.events.exception;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = -6230209086460532012L;

	public ValidationException(String errorMessage) {
		super(errorMessage);
	}
	
	public ValidationException(String errorMessage, Throwable cause) {
		super(errorMessage,cause);
	}
	
	public ValidationException(Throwable cause) {
		super(cause);
	}
}

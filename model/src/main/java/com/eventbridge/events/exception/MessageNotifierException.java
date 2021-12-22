package com.eventbridge.events.exception;

public class MessageNotifierException extends RuntimeException {
	private static final long serialVersionUID = -6230209086460532012L;

	public MessageNotifierException(String errorMessage) {
		super(errorMessage);
	}
	
	public MessageNotifierException(String errorMessage, Throwable cause) {
		super(errorMessage,cause);
	}
	
	public MessageNotifierException(Throwable cause) {
		super(cause);
	}
}

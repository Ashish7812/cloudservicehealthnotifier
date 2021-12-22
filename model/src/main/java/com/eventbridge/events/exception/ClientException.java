package com.eventbridge.events.exception;

public class ClientException extends RuntimeException {
	private static final long serialVersionUID = -6230209086460532012L;

	public ClientException(String errorMessage) {
		super(errorMessage);
	}
	
	public ClientException(String errorMessage, Throwable cause) {
		super(errorMessage,cause);
	}
	
	public ClientException(Throwable cause) {
		super(cause);
	}
}

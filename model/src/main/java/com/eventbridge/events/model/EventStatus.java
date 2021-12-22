package com.eventbridge.events.model;

public class EventStatus {
	private String status;
	
	public EventStatus() {}
	
	public EventStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

package com.eventbridge.events.model;

import com.eventbridge.events.processors.annotation.EventSourceMetadata;

@EventSourceMetadata(eventSource = EventSource.SERVICE_NOW)
public class ServicenowEvent extends Event {
	private ServiceNowEventDetail detail;
	
	public ServicenowEvent() {}

	public ServiceNowEventDetail getDetail() {
		return detail;
	}

	public void setDetail(ServiceNowEventDetail detail) {
		this.detail = detail;
	}
}

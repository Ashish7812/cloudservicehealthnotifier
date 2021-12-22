package com.eventbridge.events.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudtrailEvent extends Event {
	private CloudtrailEventDetail detail;
	
	public CloudtrailEvent() {}
	
	public CloudtrailEvent(CloudtrailEventDetail detail) {
		this.detail = detail;
	}

	public CloudtrailEventDetail getDetail() {
		return detail;
	}

	public void setDetail(CloudtrailEventDetail detail) {
		this.detail = detail;
	}
}

package com.eventbridge.events.model;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalHealthBoardEventDetail {
	private String eventArn;
	private String service;
	private String eventTypeCode;
	private String eventTypeCategory;
	private String description;

	public PersonalHealthBoardEventDetail() {}
	
	public String getEventArn() {
		return eventArn;
	}

	public void setEventArn(String eventArn) {
		this.eventArn = eventArn;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getEventTypeCode() {
		return eventTypeCode;
	}

	public void setEventTypeCode(String eventTypeCode) {
		this.eventTypeCode = eventTypeCode;
	}

	public String getEventTypeCategory() {
		return eventTypeCategory;
	}

	public void setEventTypeCategory(String eventTypeCategory) {
		this.eventTypeCategory = eventTypeCategory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}

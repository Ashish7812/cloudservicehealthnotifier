package com.eventbridge.events.model;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalHealthBoardEvent extends Event {
	private PersonalHealthBoardEventDetail detail;

	public PersonalHealthBoardEvent() {}

	public PersonalHealthBoardEventDetail getDetail() {
		return detail;
	}

	public void setDetail(PersonalHealthBoardEventDetail detail) {
		this.detail = detail;
	}
	
	@Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}

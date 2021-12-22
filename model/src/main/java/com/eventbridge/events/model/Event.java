package com.eventbridge.events.model;

import java.util.List;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
	private String id;
	private String detailType;
	private String source;
	private String account;
	private String time;
	private String region;
	private List<String> resources;
	private List<ResourceTag> resourceTags;
	
	public Event() {}
	
	public Event(String id, String detailType, String source, String account, String time, String region) {
		this.id = id;
		this.detailType = detailType;
		this.source = source;
		this.account = account;
		this.time = time;
		this.region = region;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty("detail-type")
	public String getDetailType() {
		return detailType;
	}
	
	@JsonProperty("detail-type")
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}
	
	public List<ResourceTag> getResourceTags() {
		return resourceTags;
	}

	public void setResourceTags(List<ResourceTag> resourceTags) {
		this.resourceTags = resourceTags;
	}
	
	@Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}

package com.eventbridge.events.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudtrailEventDetail {
	private String eventSource;
	private String eventName;
	private String awsRegion;
	private Map<String, String> requestParameters = new HashMap<>();
	
	public CloudtrailEventDetail() {}

	public CloudtrailEventDetail(String eventSource, 
			String eventName, 
			String awsRegion,
			Map<String, String> requestParameters) {
		
		this.eventSource = eventSource;
		this.eventName = eventName;
		this.awsRegion = awsRegion;
		this.requestParameters = requestParameters;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getAwsRegion() {
		return awsRegion;
	}

	public void setAwsRegion(String awsRegion) {
		this.awsRegion = awsRegion;
	}

	public Map<String, String> getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(Map<String, String> requestParameters) {
		this.requestParameters = requestParameters;
	}
}

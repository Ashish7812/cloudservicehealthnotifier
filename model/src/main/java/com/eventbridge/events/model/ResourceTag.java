package com.eventbridge.events.model;

import java.util.Map;

public class ResourceTag {
	private String resourceIdentifier;
	private Map<String, String> resourceTags;
	
	public ResourceTag() {}
	
	public ResourceTag(String resourceIdentifier, Map<String, String> resourceTags) {
		this.resourceIdentifier = resourceIdentifier;
		this.resourceTags = resourceTags;
	}

	public String getResourceIdentifier() {
		return resourceIdentifier;
	}

	public void setResourceIdentifier(String resourceIdentifier) {
		this.resourceIdentifier = resourceIdentifier;
	}

	public Map<String, String> getResourceTags() {
		return resourceTags;
	}

	public void setResourceTags(Map<String, String> resourceTags) {
		this.resourceTags = resourceTags;
	}
}

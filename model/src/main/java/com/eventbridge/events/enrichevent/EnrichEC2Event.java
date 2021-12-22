package com.eventbridge.events.enrichevent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eventbridge.events.clients.EC2Client;
import com.eventbridge.events.exception.ClientException;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.ResourceTag;
import com.google.inject.Inject;

public class EnrichEC2Event extends EnrichEvent {
	private EC2Client client;
	
	@Inject
	public EnrichEC2Event(EC2Client client) {
		this.client = client;
	}
	
	@Override
	public Event enrichEvent(Event inputEvent) {
		List<ResourceTag> resourceTags = new ArrayList<>();
		
		try {
			inputEvent.getResources().forEach(resource -> {
				ResourceTag resourceTag = new ResourceTag();
				Map<String, String> tags = client.describeInstanceTags(resource);
				resourceTag.setResourceIdentifier(resource);
				resourceTag.setResourceTags(tags);
				resourceTags.add(resourceTag);
			});
		} catch (Exception e) {
			String errorMessage = String.format("An error occured while describing the instance tags. Cause: %s",
					e.getMessage());
			
			throw new ClientException(errorMessage, e);
		}
		
		inputEvent.setResourceTags(resourceTags);
		return inputEvent;
	}
}

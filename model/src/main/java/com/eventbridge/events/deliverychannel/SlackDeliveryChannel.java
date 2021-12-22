package com.eventbridge.events.deliverychannel;

import com.eventbridge.events.clients.SlackClient;
import com.eventbridge.events.model.FormatMessageRequest;
import com.google.inject.Inject;

public class SlackDeliveryChannel implements DeliveryChannel {
	private SlackClient client;
	
	@Inject
	public SlackDeliveryChannel(SlackClient client) {
		this.client = client;
	}
	
	@Override
	public void sendMessage(String message, String channelIds) {
		
	}

	@Override
	public String formatMessage(FormatMessageRequest messageRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}

package com.eventbridge.events.deliverychannel;

import com.eventbridge.events.model.FormatMessageRequest;

public interface DeliveryChannel {
	public void sendMessage(String message, String channelIds);
	public String formatMessage(FormatMessageRequest messageRequest);
}

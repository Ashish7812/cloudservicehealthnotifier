package com.eventbridge.events.clients;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlackClient {
	private static final Logger LOG = LogManager.getLogger(SlackClient.class);
	
	public void notifySlackChannel(String message, String webexChannel) {
		LOG.info("Notified the webex channel " + webexChannel + " with message: " + message);
	}
}

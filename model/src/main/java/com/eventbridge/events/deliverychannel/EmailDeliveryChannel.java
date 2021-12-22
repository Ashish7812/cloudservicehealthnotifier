package com.eventbridge.events.deliverychannel;

import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.amazonaws.util.StringUtils;
import com.eventbridge.events.clients.EMailClient;
import com.eventbridge.events.exception.ClientException;
import com.eventbridge.events.model.FormatMessageRequest;
import com.google.inject.Inject;

public class EmailDeliveryChannel implements DeliveryChannel {
	private static final Logger LOG = LogManager.getLogger(EmailDeliveryChannel.class);
	private static final String EMAIL_TEMPLATE = "email-template.html";
	
	private EMailClient client;
	
	@Inject
	public EmailDeliveryChannel(EMailClient client) {
		this.client = client;
	}
	
	@Override
	public void sendMessage(String message, String channelIds) {
		try {
			Stream.of(channelIds.split(",")).forEach(channel -> {
				client.sendMail(message, channel);
			});
		} catch (Exception e) {
			String errorMessage = String.format("An error occured while sending mails to the clients. Cause: %s",
					e.getMessage());
			LOG.error(errorMessage);
			
			throw new ClientException(errorMessage);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public String formatMessage(FormatMessageRequest messageRequest) {
		try {
			String rawEmailTemplate = IOUtils
					.toString(EmailDeliveryChannel.class.getClassLoader().getResourceAsStream(EMAIL_TEMPLATE));

			String bannerColor = Strings.EMPTY;
			
			if (StringUtils.hasValue(messageRequest.getErrorMessage())) {
				bannerColor = "bgcolor=\"red\"";
			} else {
				bannerColor = "bgcolor=\"green\"";
			}
			
			return String.format(rawEmailTemplate, bannerColor, messageRequest.getEvent().getId(),
					messageRequest.getEvent().getSource(),
					messageRequest.getEventName(),
					messageRequest.getEvent().getRegion(),
					messageRequest.getEvent().getAccount(),
					messageRequest.getMessage(),
					messageRequest.getErrorMessage(),
					messageRequest.getEventDescription());
			
		} catch (Exception e) {
			String errorMessage = String.format("An error occured while reading the email template. Cause: %s",
					e.getMessage());
			
			LOG.error(errorMessage, e);
			
			throw new ClientException(errorMessage);
		}
	}
}

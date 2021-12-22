package com.eventbridge.events.clients;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.google.inject.Inject;

public class EMailClient {
	private static final Logger LOG = LogManager.getLogger(EMailClient.class);
	
	private AmazonSimpleEmailService client;
	
	@Inject
	public EMailClient(AmazonSimpleEmailService client) {
		this.client = client;
	}
	
	public void sendMail(String message, String mailId) {
		SendEmailRequest request = new SendEmailRequest()
		          .withDestination(
		              new Destination().withToAddresses(mailId))
		          .withMessage(new Message()
		              .withBody(new Body()
		                  .withHtml(new Content()
		                      .withCharset("UTF-8").withData(message)))
		              .withSubject(new Content()
		                  .withCharset("UTF-8").withData("Notification from health notifier project.")))
		          .withSource(mailId);
		
		      client.sendEmail(request);
		
		
		LOG.info("Sent a mail to " + mailId + " with message: " + message);
	}
}

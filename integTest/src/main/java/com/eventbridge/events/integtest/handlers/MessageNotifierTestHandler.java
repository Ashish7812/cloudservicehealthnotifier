package com.eventbridge.events.integtest.handlers;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.eventbridge.events.clients.EMailClient;
import com.eventbridge.events.deliverychannel.DBEventRepository;
import com.eventbridge.events.deliverychannel.DeliveryChannel;
import com.eventbridge.events.deliverychannel.EmailDeliveryChannel;
import com.eventbridge.events.guice.DeliveryChannelModule;
import com.eventbridge.events.handlers.MessageNotifierHandler;
import com.eventbridge.events.model.EventResponse;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MessageNotifierTestHandler implements RequestHandler<EventResponse, String> {
	
	@Override
	public String handleRequest(EventResponse input, Context context) {
		MessageNotifierHandler notifierHandler = new MessageNotifierHandler();
		
		Map<String, DeliveryChannel> channelTypeToDeliveryChannel = new HashMap<>();
		
		AmazonSimpleEmailService mailClient = Mockito.mock(AmazonSimpleEmailService.class);
		
		SendEmailResult results = new SendEmailResult();
		
		Mockito.when(mailClient.sendEmail(Mockito.any(SendEmailRequest.class))).thenReturn(results);
		
		EMailClient emailClient = new EMailClient(mailClient);
		
		EmailDeliveryChannel mailDeliveryChannel = new EmailDeliveryChannel(emailClient);
		
		channelTypeToDeliveryChannel.put("EMAIL", mailDeliveryChannel);
		
		notifierHandler.setChannelTypeToDeliveryChannel(channelTypeToDeliveryChannel);
		
		Injector injector = Guice.createInjector(new DeliveryChannelModule(System.getenv("Region"), System.getenv("EventsTable"),
				System.getenv("ChannelsTable")));
		
		DBEventRepository dbEventsRepository = injector.getInstance(DBEventRepository.class);
		
		notifierHandler.setEventsRepository(dbEventsRepository);
		
		return notifierHandler.handleRequest(input, context);
	}
}

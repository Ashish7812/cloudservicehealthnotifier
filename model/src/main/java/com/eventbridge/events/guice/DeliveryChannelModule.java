package com.eventbridge.events.guice;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.eventbridge.events.clients.EMailClient;
import com.eventbridge.events.clients.SlackClient;
import com.eventbridge.events.deliverychannel.ChannelRepository;
import com.eventbridge.events.deliverychannel.DBChannelRepository;
import com.eventbridge.events.deliverychannel.DBEventRepository;
import com.eventbridge.events.deliverychannel.DeliveryChannel;
import com.eventbridge.events.deliverychannel.EmailDeliveryChannel;
import com.eventbridge.events.deliverychannel.EventsRepository;
import com.eventbridge.events.deliverychannel.SlackDeliveryChannel;
import com.eventbridge.events.model.ChannelType;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class DeliveryChannelModule extends AbstractModule {
	private String region;
	private String eventsTable;
	private String channelsTable;
	
	public DeliveryChannelModule(String region, String eventsTable, String channelsTable) {
		this.region = region;
		this.eventsTable = eventsTable;
		this.channelsTable = channelsTable;
	}
	
	public void configure() {
		bind(EventsRepository.class).to(DBEventRepository.class);
		bind(ChannelRepository.class).to(DBChannelRepository.class);
	}
	
	@Provides
	@Singleton
	public Map<String, DeliveryChannel> provideDeliveryChannels(AmazonSimpleEmailService sesClient) {
		Map<String, DeliveryChannel> channelTypeToDeliveryChannel = new HashMap<>();
		channelTypeToDeliveryChannel.put(ChannelType.EMAIL.name(), new EmailDeliveryChannel(new EMailClient(sesClient)));
		channelTypeToDeliveryChannel.put(ChannelType.SLACK.name(), new SlackDeliveryChannel(new SlackClient()));
		
		return channelTypeToDeliveryChannel;
	}
	
	@Provides
	@Singleton
	public DBChannelRepository provideChannelDetails(@Named("ChannelsTable") Table table) {
		return new DBChannelRepository(table);
	}
	
	@Provides
	@Singleton
	public DBEventRepository provideEventsRepository(@Named("EventsTable") Table table) {
		return new DBEventRepository(table);
	}
	
	@Provides
	@Singleton
	public AmazonSimpleEmailService provideEmailClient() {
		return AmazonSimpleEmailServiceClientBuilder.standard().build();
	}
	
	@Provides
	@Singleton
	@Named("ChannelsTable")
	public Table provideChannelsDynamoTable(DynamoDB dynamoDB) {
		return dynamoDB.getTable(channelsTable);
	}
	
	@Provides
	@Singleton
	@Named("EventsTable")
	public Table provideEventsDynamoTable(DynamoDB dynamoDB) {
		return dynamoDB.getTable(eventsTable);
	}
	
	@Provides
	@Singleton
	public DynamoDB provideDynamoDB() {
		return new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(region).build());
	}
}

package com.eventbridge.events.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public abstract class NotificationHandlerGuiceModule {
	private String stage = System.getenv("Stage");
	
	public void injectMembers(String eventSource, String eventName, String eventPayload, boolean isCloudTrailEvent) {
		if (Stage.PRODUCTION.name().equals(stage)) {
			EventsProcessorGuiceModule eventProcessModule = new EventsProcessorGuiceModule(eventSource, eventName,
					eventPayload, isCloudTrailEvent);

			Injector injector = Guice.createInjector(eventProcessModule,
					new ExternalServiceClientGuiceModule(System.getenv("Region")), new DeliveryChannelModule(
							System.getenv("Region"), System.getenv("EventsTable"), System.getenv("ChannelsTable")));

			injector.injectMembers(this);
		}	
	}
	
	public void injectNotifierMembers() {
		if (Stage.PRODUCTION.name().equals(stage)) {
			Injector injector = Guice.createInjector(new DeliveryChannelModule(System.getenv("Region"),
					System.getenv("EventsTable"), System.getenv("ChannelsTable")));
			injector.injectMembers(this);
		}
	}
}

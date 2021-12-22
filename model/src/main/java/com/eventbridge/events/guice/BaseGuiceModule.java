package com.eventbridge.events.guice;

import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;

import com.eventbridge.events.exception.UnknownEventException;
import com.eventbridge.events.processors.DefaultEventProcessor;
import com.eventbridge.events.processors.annotation.EventProcessorMetadata;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BaseGuiceModule extends AbstractModule {
	
	@SuppressWarnings("unchecked")
	public <EventsProcessor> EventsProcessor provideEventProcessorInstance(Class<EventsProcessor> clazz,
			String classPackageLocation, String eventSource, String eventType) {
	
		Reflections reflections = new Reflections(classPackageLocation);

		Set<Class<? extends EventsProcessor>> types = reflections.getSubTypesOf(clazz);

		Optional<Class<? extends EventsProcessor>> eventProcessorClass = types.stream()
				.filter(t -> t.getAnnotation(EventProcessorMetadata.class) != null
						&& t.getAnnotation(EventProcessorMetadata.class).eventSource().equals(eventSource)
						&& (!eventType.isEmpty()
								? t.getAnnotation(EventProcessorMetadata.class).eventType().equals(eventType) : true))
				.findAny();

		if (eventProcessorClass.isPresent()) {
			try {
				EventsProcessor eventProcessor =  (EventsProcessor) eventProcessorClass.get().newInstance();
				
				Injector injector = Guice.createInjector(new ExternalServiceClientGuiceModule(System.getenv("Region")),
						new DeliveryChannelModule(System.getenv("Region"), System.getenv("EventsTable"),
								System.getenv("ChannelsTable")));

				injector.injectMembers(eventProcessor);
				
				return eventProcessor;
				
			} catch (Exception e) {
				throw new UnknownEventException("The event processor class could not be found.", e);
			}
		}
		
		return (EventsProcessor) new DefaultEventProcessor();
	}
	
}

package com.eventbridge.events.processors.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventProcessorMetadata {
	String eventProcessorName();
	String eventSource();
	String eventType();
	String eventDescription();
}

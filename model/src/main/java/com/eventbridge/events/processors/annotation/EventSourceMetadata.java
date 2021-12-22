package com.eventbridge.events.processors.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventSourceMetadata {
	String eventSource();
}

package com.eventbridge.events.model;

/**
 * This class holds all the events sources.
 * 
 * @author sahoa
 *
 */
public class EventSource {
	public static final String AWS_S3 = "aws.s3";
	public static final String AWS_HEALTH = "aws.health";
	public static final String SERVICE_NOW = "servicenow";
	
	public static boolean isCloudTrailEvent(String eventSource) {
		if (!eventSource.equals(AWS_HEALTH) && !eventSource.equals(SERVICE_NOW)) {
			return true;
		} else {
			return false;
		}
	}
}

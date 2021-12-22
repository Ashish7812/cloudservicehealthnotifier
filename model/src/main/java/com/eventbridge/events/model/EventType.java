package com.eventbridge.events.model;

public class EventType {
	public static class PHD_EC2 {
		private PHD_EC2() {}
		
		public static final String EC2_DNS_RESOLUTION_ISSUE = "AWS_EC2_DNS_RESOLUTION_ISSUE";
		public static final String EC2_AUTO_RECOVERRY_SUCCESS = "AWS_EC2_INSTANCE_AUTO_RECOVERY_SUCCESS";
		public static final String EC2_PERSISTENT_INSTANCE_RETIREMENT_SCHEDULED  = "AWS_EC2_PERSISTENT_INSTANCE_RETIREMENT_SCHEDULED";
	}
	
	public static class S3 {
		private S3() {}
		
		public static final String PUT_OBJECT = "PutObject";
	}
	
	public static class NOW {
		private NOW() {}
		
		public static final String CREATE_TICKET = "NOW_CREATE_TICKET";
	}
}

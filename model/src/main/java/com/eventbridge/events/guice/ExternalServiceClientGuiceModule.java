package com.eventbridge.events.guice;

import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.eventbridge.events.clients.CloudformationClient;
import com.eventbridge.events.clients.EC2Client;
import com.eventbridge.events.clients.SlackClient;
import com.eventbridge.events.clients.StepFunctionClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * This class holds all the external service clients creation logic.
 * 
 * @author sahoa
 *
 */
public class ExternalServiceClientGuiceModule extends AbstractModule {
	
	private String region;
	
	public ExternalServiceClientGuiceModule(String region) {
		this.region = region;
	}
	
	@Provides
	@Singleton
	public SlackClient provideWebexClient() {
		return new SlackClient();
	}
	
	@Provides
	@Singleton
	public EC2Client provideEC2Client() {
		return new EC2Client(AmazonEC2ClientBuilder.standard().withRegion(region).build());
	}
	
	@Provides
	@Singleton
	public StepFunctionClient provideStepFunctionClient() {
		return new StepFunctionClient(AWSStepFunctionsClientBuilder.standard().withRegion(region).build());
	}
	
	@Provides
	@Singleton
	public CloudformationClient provideCloudformationClient() {
		return new CloudformationClient(AmazonCloudFormationClientBuilder.standard().withRegion(region).build());
	}
}

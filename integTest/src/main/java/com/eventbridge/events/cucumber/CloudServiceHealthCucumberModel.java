package com.eventbridge.events.cucumber;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.stepfunctions.model.DescribeExecutionResult;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.eventbridge.events.clients.CloudformationClient;
import com.eventbridge.events.clients.StepFunctionClient;
import com.eventbridge.events.deliverychannel.DBEventRepository;
import com.eventbridge.events.guice.DeliveryChannelModule;
import com.eventbridge.events.guice.ExternalServiceClientGuiceModule;
import com.eventbridge.events.model.QueryEventRequest;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class CloudServiceHealthCucumberModel {
	private CloudformationClient cfnClient;
	private StepFunctionClient stepFnClient;
	private DBEventRepository eventsRepository;
	
	private String stackName;
	private String region;
	private String eventsTable;
	private String channelsTable;
	private String stepFunctionARN;
	private String executionARN;
	
	public CloudServiceHealthCucumberModel() {
		this.stackName = System.getProperty("stackName");
		this.region = System.getProperty("region");
		
		Injector injector = Guice.createInjector(new ExternalServiceClientGuiceModule(region));

		this.cfnClient = injector.getInstance(CloudformationClient.class);
		this.stepFnClient = injector.getInstance(StepFunctionClient.class);
		
		Optional<Stack> stack = cfnClient.findStack(stackName);
		
		if (stack.isPresent()) {
			Map<String, String> outputs = CloudformationClient.outputsOf(stack.get());
			this.eventsTable = outputs.get("EventsTable");
			this.channelsTable = outputs.get("ChannelsTable");
			this.stepFunctionARN = outputs.get("StepFunctionARN");
		} else {
			throw new IllegalArgumentException(String.format("The stack %s is not present.", stackName));
		}
		
		injector = Guice.createInjector(new DeliveryChannelModule(region, eventsTable, channelsTable));
		
		this.eventsRepository = injector.getInstance(DBEventRepository.class);
	}
	
	@Given("^The custom myhealth event is fired with event payload (.+)$")
	public void the_custom_event_is_fired(String key) throws IOException {
		StartExecutionResult result = stepFnClient.startStateMachineExecution(stepFunctionARN,
				readEventPayload(formatInput(key)));
		executionARN = result.getExecutionArn();
	}
	
	@When("^The event is polled for (.+)$")
	public void the_custom_event_is_inspected(String status) throws InterruptedException {
		DescribeExecutionResult result = stepFnClient.describeStateMachineExecution(executionARN);
		
		while (!result.getStatus().equals(formatInput(status))) {
			result = stepFnClient.describeStateMachineExecution(executionARN);
			Thread.sleep(2000);
		}
		
		Assert.assertEquals(formatInput(status), result.getStatus());
	}
	
	@Then("^The event is failed with error message (.+)$")
	public void the_custom_event_notification_is_received(String message) {
		String errorMessage = eventsRepository.readErrorMessage(new QueryEventRequest("aws.health",
				"AWS_EC2_INSTANCE_AUTO_RECOVERY_SUCCESS", "12719fa9-bca9-936c-d06c-40adc062b6bd"));

		Assert.assertEquals(formatInput(message), errorMessage);
	}

	@SuppressWarnings("deprecation")
	private static String readEventPayload(String key) throws IOException {
		return IOUtils.toString(CloudServiceHealthCucumberModel.class.getClassLoader()
				.getResourceAsStream("events/" + key));
	}
	
	private String formatInput(String input) {
		return input.replace("\"", "");
	}
}

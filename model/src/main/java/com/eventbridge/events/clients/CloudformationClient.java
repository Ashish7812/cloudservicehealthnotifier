package com.eventbridge.events.clients;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Output;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.util.StringUtils;
import com.eventbridge.events.exception.ClientException;
import com.google.inject.Inject;

public class CloudformationClient {
	private static final Logger LOG = LoggerFactory.getLogger(CloudformationClient.class);
	
	private final AmazonCloudFormation client;
	
	@Inject
	public CloudformationClient(AmazonCloudFormation client) {
		this.client = client;
	}
	
	public Optional<Stack> findStack(String stackName) {
		if (StringUtils.isNullOrEmpty(stackName)) {
			return Optional.empty();
		}

		try {
			DescribeStacksResult result = client.describeStacks(new DescribeStacksRequest().withStackName(stackName));

			if (!result.getStacks().isEmpty()) {
				return result.getStacks().stream().findAny();
			}
		} catch (AmazonServiceException ase) {
			LOG.error("Stack lookup failed to find stack, assuming non-existant: {}", stackName);
			if (ase.getErrorCode().equals("ValidationError")) {
				LOG.info("Stack lookup failed to find stack, assuming non-existant: {}", stackName);
			} else if (ase.getErrorCode().equals("InvalidClientTokenId")) {
				LOG.info(
						"Stack lookup failed to find stack, assuming non-existant stack {} with error InvalidClientTokenId",
						stackName);
			} else {
				throw new ClientException(String.format("Failure looking up stack: %s with error message %s",
						stackName, ase.getErrorMessage()));
			}
		}

		return Optional.empty();
	}
	
	public static Map<String, String> outputsOf(Stack stack) {
		return stack.getOutputs().stream().collect(Collectors.toMap(Output::getOutputKey, Output::getOutputValue));
	}
}

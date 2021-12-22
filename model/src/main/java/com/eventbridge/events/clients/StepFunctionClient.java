package com.eventbridge.events.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.DescribeExecutionRequest;
import com.amazonaws.services.stepfunctions.model.DescribeExecutionResult;
import com.amazonaws.services.stepfunctions.model.ExecutionLimitExceededException;
import com.amazonaws.services.stepfunctions.model.InvalidArnException;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.amazonaws.services.stepfunctions.model.StateMachineDeletingException;
import com.amazonaws.services.stepfunctions.model.StateMachineDoesNotExistException;
import com.google.inject.Inject;

public class StepFunctionClient {
	private static final Logger LOG = LoggerFactory.getLogger(StepFunctionClient.class);
	
	private AWSStepFunctions client;

	@Inject
	public StepFunctionClient(AWSStepFunctions client) {
		this.client = client;
	}

	/**
	 * This method returns the status of execution.
	 * 
	 * @param executionArn
	 *            particular execution Arn
	 * @return
	 */
	public DescribeExecutionResult describeStateMachineExecution(String executionArn) {
		DescribeExecutionResult executionStatus = null;
		
		try {
			DescribeExecutionRequest describeExecutionRequest = new DescribeExecutionRequest();
			describeExecutionRequest.setExecutionArn(executionArn);
			executionStatus = client.describeExecution(describeExecutionRequest);
		} catch (StateMachineDoesNotExistException e) {
			LOG.error(String.format("State machine %s doesn't exist", e.getMessage()), e);
		} catch (InvalidArnException e) {
			LOG.error(String.format("Execution ID %s doesn't exist", executionArn), e);
		} catch (Exception e) {
			LOG.error(String.format("An error ocurred for Execution Arn %s.", executionArn), e);
		}
		
		return executionStatus;
	}

	/**
	 * This method helps in starting the state machine execution
	 * 
	 * @param stateMachineArn
	 * @param input
	 * @return
	 */
	public StartExecutionResult startStateMachineExecution(String stateMachineArn, String input) {
		StartExecutionResult startExecutionResult = null;
		try {
			StartExecutionRequest startExecutionRequest = new StartExecutionRequest();
			startExecutionRequest.setStateMachineArn(stateMachineArn);
			startExecutionRequest.setInput(input);
			startExecutionResult = client.startExecution(startExecutionRequest);
		} catch (StateMachineDeletingException | StateMachineDoesNotExistException e) {
			LOG.error(String.format("State machine %s doesn't exist or delete in progress", stateMachineArn), e);
		} catch (ExecutionLimitExceededException e) {
			LOG.error(String.format(
					"The maximum number of running executions has been reached for state machine %s. Please retry after sometime",
					stateMachineArn), e);
		}
		return startExecutionResult;
	}
}


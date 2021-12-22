package com.eventbridge.events.clients;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class EC2Client {
	private AmazonEC2 ec2Client;
	
	public EC2Client(AmazonEC2 ec2Client) {
		this.ec2Client = ec2Client;
	}

	public Map<String, String> describeInstanceTags(String instanceId) {
		Map<String, String> tagsInfo = new HashMap<>();
		
		if (instanceId.startsWith("i-")) {
			DescribeInstancesResult result = ec2Client
					.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId));

			result.getReservations().get(0).getInstances().get(0).getTags().forEach(tag -> {
				tagsInfo.put(tag.getKey(), tag.getValue());
			});
		}
		
		return tagsInfo;
	}
	
	public void stopInstance(String instanceId) {
		ec2Client.stopInstances(new StopInstancesRequest().withInstanceIds(instanceId));
	}
	
	public void startInstance(String instanceId) {
		ec2Client.startInstances(new StartInstancesRequest().withInstanceIds(instanceId));
	}
}

package com.eventbridge.events.integtest.handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mockito.Mockito;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.eventbridge.events.clients.EC2Client;
import com.eventbridge.events.deliverychannel.DBChannelRepository;
import com.eventbridge.events.deliverychannel.DBEventRepository;
import com.eventbridge.events.enrichevent.EnrichEC2Event;
import com.eventbridge.events.guice.DeliveryChannelModule;
import com.eventbridge.events.handlers.EventProcessorHandler;
import com.eventbridge.events.model.CloudtrailEvent;
import com.eventbridge.events.model.EventSource;
import com.eventbridge.events.model.PersonalHealthBoardEvent;
import com.eventbridge.events.processors.PersonalHealthBoardEC2EventProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class EventProcessorTestHandler implements RequestStreamHandler {
	
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		EventProcessorHandler processorHandler = new EventProcessorHandler();
		
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode payload = mapper.readTree(input);
		
		String eventData = mapper.writeValueAsString(payload);
		
		String eventSource = payload.get("source").asText();
		
		String detail = payload.get("detail-type").asText();
		
		if (EventSource.AWS_HEALTH.equals(eventSource)) {
			processorHandler.setEvent(mapper.readValue(eventData, PersonalHealthBoardEvent.class));
		}
		
		if (EventSource.isCloudTrailEvent(eventSource)) {
			processorHandler.setEvent(mapper.readValue(eventData, CloudtrailEvent.class));
		}
		
		AmazonEC2 ec2NativeClient = Mockito.mock(AmazonEC2.class);
		
		mockEC2Client(ec2NativeClient, detail);
		
		EC2Client ec2Client = new EC2Client(ec2NativeClient);
		
		EnrichEC2Event enrichEC2Event = new EnrichEC2Event(ec2Client);
		
		PersonalHealthBoardEC2EventProcessor healthProcessor = new PersonalHealthBoardEC2EventProcessor();
		healthProcessor.setEnrichEC2Event(enrichEC2Event);
		
		processorHandler.setProcessor(healthProcessor);
		
		Injector injector = Guice.createInjector(new DeliveryChannelModule(System.getenv("Region"), System.getenv("EventsTable"),
				System.getenv("ChannelsTable")));
		
		DBEventRepository dbEventsRepository = injector.getInstance(DBEventRepository.class);
		DBChannelRepository dbChannelRepository = injector.getInstance(DBChannelRepository.class);
		
		processorHandler.setChannelRepository(dbChannelRepository);
		processorHandler.setEventsRepository(dbEventsRepository);
		
		InputStream stream = new ByteArrayInputStream(eventData.getBytes(StandardCharsets.UTF_8));
		
		processorHandler.handleRequest(stream, output, context);
	}

	private void mockEC2Client(AmazonEC2 ec2NativeClient, String detail) {
		Map<String, String> tags = new HashMap<>();
		tags.put("Owner", "MogliCloud");
		tags.put("Department", "Jungle");
		
		List<Tag> ec2Tags = new ArrayList<>();
		
		tags.forEach((key, value) -> {
			Tag tag = new Tag();
			tag.setKey(key);
			tag.setValue(value);
			
			ec2Tags.add(tag);
		});
		
		List<Instance> instances = new ArrayList<>();
		Instance instance = new Instance();
		instance.setTags(ec2Tags);
		instances.add(instance);
		
		List<Reservation> reservations = new ArrayList<>();
		Reservation reservation = new Reservation();
		reservation.setInstances(instances);
		reservations.add(reservation);
		
		DescribeInstancesResult results = new DescribeInstancesResult();
		results.setReservations(reservations);

		if (detail.equals("This event processing will fail.")) {
			Mockito.when(ec2NativeClient.describeInstances(Mockito.any(DescribeInstancesRequest.class)))
					.thenThrow(new RuntimeException("Invalid instance id."));
		} else {
			Mockito.when(ec2NativeClient.describeInstances(Mockito.any(DescribeInstancesRequest.class)))
					.thenReturn(results);
		}
	}
}

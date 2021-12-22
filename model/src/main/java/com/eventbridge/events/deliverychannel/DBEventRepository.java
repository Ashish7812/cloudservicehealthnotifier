package com.eventbridge.events.deliverychannel;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.util.json.Jackson;
import com.eventbridge.events.model.CloudtrailEvent;
import com.eventbridge.events.model.DispatcherInfo;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.EventInfo;
import com.eventbridge.events.model.EventSource;
import com.eventbridge.events.model.PersonalHealthBoardEvent;
import com.eventbridge.events.model.QueryEventRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DBEventRepository implements EventsRepository {
	private static final Logger LOG = LogManager.getLogger(DBEventRepository.class);
	
	private Table eventsTable;
	
	@Inject
	public DBEventRepository(@Named("EventsTable") Table eventsTable) {
		this.eventsTable = eventsTable;
	}
	
	@Override
	public boolean isExistingEvent(QueryEventRequest request) {
		Item item = getCurrentItem(request);

		if (item != null) {
			return true;
		}

		return false;
	}

	@Override
	public EventInfo readEvent(QueryEventRequest request) {
		Item item = getCurrentItem(request);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		if (item != null) {
			try {
				String eventSource = item.getString("EventSource");
				String eventId = item.getString("EventID");
				String errorMessage = item.getString("ErrorMessage");
				String payLoad = item.getJSON("EventPayload");
				String dispatherInfo = item.getJSON("DispatcherInfo");
				
				EventInfo eventInfo = new EventInfo();
				eventInfo.setEventSource(eventSource);
				eventInfo.setEventId(eventId);
				eventInfo.setErrorMessage(errorMessage);
				eventInfo.setDispatherInfo(objectMapper.readValue(dispatherInfo, DispatcherInfo.class));
				
				if (eventSource.equals(EventSource.AWS_HEALTH)) {
					eventInfo.setEvent(objectMapper.readValue(payLoad, PersonalHealthBoardEvent.class));
				} else if (EventSource.isCloudTrailEvent(eventSource)){
					eventInfo.setEvent(objectMapper.readValue(payLoad, CloudtrailEvent.class));
				} else {
					eventInfo.setEvent(objectMapper.readValue(payLoad, Event.class));
				}
				
				return eventInfo;
			} catch (IOException e) {
				LOG.error("Unable to parse the JSON response", e);
			}
		}
		
		return new EventInfo();
	}
	

	@Override
	public String readEventAsString(QueryEventRequest request) {
		Item item = getCurrentItem(request);
		
		if (item != null) {
			return item.toJSON();
		}
		
		return Strings.EMPTY;
	}

	private Item getCurrentItem(QueryEventRequest request) {
		LOG.info("Get the event info from DB having request {}", request);
		
		GetItemSpec spec = new GetItemSpec().withConsistentRead(true)
				.withPrimaryKey("EventSource", request.getEventSource(), "EventID", request.getSortKey());
		
		return eventsTable.getItem(spec);
	}
	
	@Override
	public void putEvent(QueryEventRequest request, Event event) {
		eventsTable.updateItem(
				new PrimaryKey("EventSource", request.getEventSource(), "EventID", request.getSortKey()),
				"set #SD = :val",
				new NameMap().with("#SD", "EventPayload"),
				new ValueMap().withJSON(":val", Jackson.toJsonString(event)));
	}

	@Override
	public void putErrorMessage(QueryEventRequest request, String errorMessage) {
		eventsTable.updateItem(
				new PrimaryKey("EventSource", request.getEventSource(), "EventID", request.getSortKey()),
				"set #SD = :val",
				new NameMap().with("#SD", "ErrorMessage"),
				new ValueMap().withString(":val", errorMessage));
	}

	@Override
	public void putDispatcherInfo(QueryEventRequest request, DispatcherInfo dispatherInfo) {
		eventsTable.updateItem(
				new PrimaryKey("EventSource", request.getEventSource(), "EventID", request.getSortKey()),
				"set #SD = :val",
				new NameMap().with("#SD", "DispatcherInfo"),
				new ValueMap().withJSON(":val", Jackson.toJsonString(dispatherInfo)));
	}

	@Override
	public String readErrorMessage(QueryEventRequest request) {
		Item item = getCurrentItem(request);
		return item.getString("ErrorMessage");
	}

	@Override
	public String readEventDescription(QueryEventRequest request) {
		Item item = getCurrentItem(request);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		String payLoad = item.getJSON("EventPayload");
		
		try {
			if (request.getEventSource().equals(EventSource.AWS_HEALTH)) {
				return objectMapper.readValue(payLoad, PersonalHealthBoardEvent.class).getDetail().getDescription();
			}
		} catch (IOException e) {
			LOG.error("Unable to parse the JSON response", e);
		}
		
		return "";
	}
}

package com.eventbridge.events.deliverychannel;

import com.eventbridge.events.model.DispatcherInfo;
import com.eventbridge.events.model.Event;
import com.eventbridge.events.model.EventInfo;
import com.eventbridge.events.model.QueryEventRequest;

public interface EventsRepository {
	public boolean isExistingEvent(QueryEventRequest request);
	
	public EventInfo readEvent(QueryEventRequest request);
	
	public String readEventDescription(QueryEventRequest request);
	
	public String readEventAsString(QueryEventRequest request);
	
	public void putEvent(QueryEventRequest request, Event event);
	
	public void putErrorMessage(QueryEventRequest request, String errorMessage);
	
	public String readErrorMessage(QueryEventRequest request);
	
	public void putDispatcherInfo(QueryEventRequest request, DispatcherInfo dispatherInfo);
}

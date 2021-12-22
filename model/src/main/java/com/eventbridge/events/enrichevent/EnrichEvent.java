package com.eventbridge.events.enrichevent;

import com.eventbridge.events.model.Event;

public abstract class EnrichEvent {
	public abstract Event enrichEvent(Event inputEvent);
}

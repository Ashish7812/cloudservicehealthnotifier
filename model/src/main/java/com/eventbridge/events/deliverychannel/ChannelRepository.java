package com.eventbridge.events.deliverychannel;

import java.util.List;

public interface ChannelRepository {
	public List<ChannelInfo> readChannelDetails(String eventCode);
}

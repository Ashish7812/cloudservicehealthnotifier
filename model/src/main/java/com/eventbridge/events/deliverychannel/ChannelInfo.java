package com.eventbridge.events.deliverychannel;

import com.amazonaws.util.json.Jackson;

public class ChannelInfo {
	private String channelType;
	private String channelIds;
	
	public ChannelInfo() {}
	
	public ChannelInfo(String channelType, String channelIds) {
		this.channelType = channelType;
		this.channelIds = channelIds;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getChannelIds() {
		return channelIds;
	}

	public void setChannelIds(String channelIds) {
		this.channelIds = channelIds;
	}
	
	@Override
	public String toString() {
		return Jackson.toJsonString(this);
	}
}

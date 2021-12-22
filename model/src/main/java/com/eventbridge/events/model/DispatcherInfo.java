package com.eventbridge.events.model;

import java.util.List;

import com.amazonaws.util.json.Jackson;
import com.eventbridge.events.deliverychannel.ChannelInfo;

public class DispatcherInfo {
	private String message;
	private List<ChannelInfo> channelInfos;
	
	public DispatcherInfo() {}
	
	public DispatcherInfo(String message, List<ChannelInfo> channelInfos) {
		this.message = message;
		this.channelInfos = channelInfos;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ChannelInfo> getChannelInfos() {
		return channelInfos;
	}

	public void setChannelInfos(List<ChannelInfo> channelInfos) {
		this.channelInfos = channelInfos;
	}
	
	@Override
	public String toString() {
		return Jackson.toJsonString(this);
	}
}

package com.eventbridge.events.deliverychannel;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DBChannelRepository implements ChannelRepository {
	private static final Logger LOG = LogManager.getLogger(DBChannelRepository.class);
	
	private Table channelTable;
	
	@Inject
	public DBChannelRepository(@Named("ChannelsTable") Table channelTable) {
		this.channelTable = channelTable;
	}

	@Override
	public List<ChannelInfo> readChannelDetails(String eventCode) {
		LOG.info("Read the channel details of event code" + eventCode);
		
		List<ChannelInfo> channelInfo = new ArrayList<>();
		
		QuerySpec spec = new QuerySpec()
				.withHashKey("EventCode", eventCode);

		ItemCollection<QueryOutcome> items = channelTable.query(spec);
		
		items.forEach(item -> {
			channelInfo.add(new ChannelInfo(item.getString("ChannelType"), item.getString("ChannelIDs")));
		});
		
		return channelInfo;
	}
}	

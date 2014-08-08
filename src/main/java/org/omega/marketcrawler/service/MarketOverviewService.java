package org.omega.marketcrawler.service;

import java.util.Map;

import org.omega.marketcrawler.entity.MarketOverview;

public class MarketOverviewService extends SimpleDBService<MarketOverview> {

	static {
		columnToProperty.put("item_id", "itemId");
		columnToProperty.put("start_time", "startTime");
		columnToProperty.put("end_time", "endTime");

		columnToProperty.put("watched_vol", "watchedVol");
		columnToProperty.put("exchange_vol", "exchangeVol");
	}

	protected Map<String, String> getColumnToProperty() {
		return columnToProperty;
	}

	protected String getTableName() {
		return "market_overview";
	}

}

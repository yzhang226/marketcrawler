package org.omega.marketcrawler.service;

import java.util.Map;

import org.omega.marketcrawler.entity.TradeBuyStatistics;

public class TradeBuyStatisticsService extends SimpleDBService<TradeBuyStatistics> {
	
	static {
		columnToProperty.put("item_id", "itemId");
		columnToProperty.put("start_time", "startTime");
		columnToProperty.put("end_time", "endTime");
		columnToProperty.put("buy_watched_vol", "buyWatchedVol");
		columnToProperty.put("buy_exchange_vol", "buyExchangeVol");
		columnToProperty.put("avg_buy_price", "avgBuyPrice");
		columnToProperty.put("buy_count", "buyCount");
	}
	
	public TradeBuyStatisticsService() {
		super();
	}

	protected Map<String, String> getColumnToProperty() {
		return columnToProperty;
	}
	
	protected String getTableName() {
		return "trade_buy_stat_one_minute";
	}
	
	public static void main(String[] args) {
		TradeBuyStatisticsService buystatser = new TradeBuyStatisticsService();
		System.out.println(buystatser.clz);
		System.out.println(buystatser.clz.getSimpleName());
	}
	
}

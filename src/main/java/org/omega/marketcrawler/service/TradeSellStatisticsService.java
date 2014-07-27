package org.omega.marketcrawler.service;

import java.sql.SQLException;
import java.util.Map;

import org.omega.marketcrawler.entity.TradeSellStatistics;

public class TradeSellStatisticsService extends SimpleDBService<TradeSellStatistics> {

	static {
		columnToProperty.put("item_id", "itemId");
		columnToProperty.put("start_time", "startTime");
		columnToProperty.put("end_time", "endTime");
		columnToProperty.put("sell_watched_vol", "sellWatchedVol");
		columnToProperty.put("sell_exchange_vol", "sellExchangeVol");
		columnToProperty.put("avg_sell_price", "avgSellPrice");
		columnToProperty.put("sell_count", "sellCount");
	}
	
	public TradeSellStatisticsService() {
		super();
	}

	protected Map<String, String> getColumnToProperty() {
		return columnToProperty;
	}
	
	protected String getTableName() {
		return "trade_sell_stat_one_minute";
	}

	public static void main(String[] args) throws SQLException {
		TradeSellStatisticsService ser = new TradeSellStatisticsService();
		System.out.println(ser.clz);
		System.out.println(ser.clz.getSimpleName());
		System.out.println(ser.findAll());
	}
	
}

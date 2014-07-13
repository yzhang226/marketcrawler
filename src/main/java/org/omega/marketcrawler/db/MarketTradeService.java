package org.omega.marketcrawler.db;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public class MarketTradeService {
	
	protected String getMarketTradeTable(WatchListItem item) {
		return Utils.getMarketTradeTable(item);
	}
	
	protected String preparedSql4MarketTrade(WatchListItem item) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(getMarketTradeTable(item)).append(" (")
		  .append("trade_time, trade_type, price, total_units, total_cost").append(") VALUES (?, ?, ?, ?, ?)")
		  .append(" ON DUPLICATE KEY UPDATE total_cost=total_cost");
		return sb.toString();
	}
	
	protected String createSql4MarketTrade(WatchListItem item) {
		StringBuilder create = new StringBuilder();
		create.append("CREATE TABLE ").append(getMarketTradeTable(item)).append(" ( ").append("\n")
		      .append("trade_time BIGINT NOT NULL , ").append("\n")
		      .append("trade_type TINYINT NULL , ").append("\n")
		      .append("price DOUBLE NULL ,").append("\n")
		      .append("total_units DOUBLE NULL ,").append("\n")
		      .append("total_cost DOUBLE NULL ,").append("\n")
		      .append("PRIMARY KEY (trade_time) );");
		
		return create.toString();
	}
	
	public boolean existWatchedTable(WatchListItem item) throws SQLException {
		return DbManager.inst().existTable(getMarketTradeTable(item));
	}
	
	public int createWatchedTable(WatchListItem item) throws SQLException {
		return DbManager.inst().execute(createSql4MarketTrade(item));
	}
	
	public int[] save(WatchListItem item, List<MarketTrade> records) throws SQLException {
		Object[][] params = new Object[records.size()][5];
		for (int i=0; i<records.size(); i++) {
			params[i][0] = records.get(i).getTradeTime();
			params[i][1] = records.get(i).getTradeType();
			params[i][2] = records.get(i).getPrice();
			params[i][3] = records.get(i).getTotalUnits();
			params[i][4] = records.get(i).getTotalCost();
		}
		return DbManager.inst().batch(preparedSql4MarketTrade(item), params);
	}
	
	public Long getCount(WatchListItem item) throws SQLException {
		ResultSetHandler<Object[]> handler = new ArrayHandler();
		Object[] resu = (Object[]) DbManager.inst().query("select count(*) from " + getMarketTradeTable(item), handler);
		return (Long) resu[0];
	}
	
	public List<Long> findAllTradeTimes(WatchListItem item) throws SQLException {
		ColumnListHandler<Long> handler = new ColumnListHandler<>(1);
		List<Long> resu = DbManager.inst().query("select trade_time from " + getMarketTradeTable(item), handler);
		return resu;
	}
	
	public static void main(String[] args) throws SQLException {
		WatchListItem item = new WatchListItem("mintpal", "VRC", Symbol.BTC.name());
		MarketTradeService tser = new MarketTradeService();
//		System.out.println(tser.getCount(item));
		System.out.println(tser.findAllTradeTimes(item));
	}
	
}

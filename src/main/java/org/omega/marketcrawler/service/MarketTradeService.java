package org.omega.marketcrawler.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.DbManager;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.operator.Mintpal;

public class MarketTradeService extends SimpleDBService<MarketTrade> {
	
	static {
		columnToProperty.put("trade_time", "tradeTime");
		columnToProperty.put("trade_type", "tradeType");
		columnToProperty.put("total_units", "totalUnits");
		columnToProperty.put("total_cost", "totalCost");
		columnToProperty.put("trade_id", "tradeId");
		columnToProperty.put("nano_time", "nanoTime");
	}
	
	protected Map<String, String> getColumnToProperty() {
		return columnToProperty;
	}
	
	protected String getTableName() {
		return null;
	}
	
	protected String preparedInsertSql4MarketTrade(WatchListItem item) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(item.toMarketTradeTable()).append(" (")
		  .append("trade_time, trade_type, price, total_units, total_cost, trade_id, nano_time").append(") VALUES (?, ?, ?, ?, ?, ?, ?)");
//		  .append(" ON DUPLICATE KEY UPDATE total_cost=VALUES(total_cost), total_units=VALUES(total_units), price=VALUES(price)");
		return sb.toString();
	}
	
	protected String createSql4MarketTrade(WatchListItem item) {
		StringBuilder create = new StringBuilder();
		create.append("CREATE TABLE ").append(item.toMarketTradeTable()).append(" ( ").append("\n")
		 	  .append("id INT NOT NULL AUTO_INCREMENT, ").append("\n")
		      .append("trade_time BIGINT NOT NULL , ").append("\n")
		      .append("trade_type TINYINT NOT NULL , ").append("\n")
		      .append("price DOUBLE NULL ,").append("\n")
		      .append("total_units DOUBLE NULL ,").append("\n")
		      .append("total_cost DOUBLE NULL ,").append("\n")
		      .append("trade_id INT NULL ,").append("\n")
		      .append("nano_time TINYINT NULL , ").append("\n")
		      .append("PRIMARY KEY (id) );");
		return create.toString();
	}
	
	public boolean existWatchedTable(WatchListItem item) throws SQLException {
		return dbManager.existTable(item.toMarketTradeTable());
	}
	
	public int createWatchedTable(WatchListItem item) throws SQLException {
		return execute(createSql4MarketTrade(item));
	}
	
	public boolean initWatchedTable(WatchListItem item) throws SQLException {
		if (!existWatchedTable(item)) {
			createWatchedTable(item);
			return true;
		}
		return false;
	}
	
	public int[] save(WatchListItem item, List<MarketTrade> records) throws SQLException {
		Object[][] params = new Object[records.size()][7];
		MarketTrade mt = null;
		for (int i=0; i<records.size(); i++) {
			mt = records.get(i);
			params[i][0] = mt.getTradeTime();
			params[i][1] = mt.getTradeType();
			params[i][2] = mt.getPrice();
			params[i][3] = mt.getTotalUnits();
			params[i][4] = mt.getTotalCost();
			params[i][5] = mt.getTradeId();
			params[i][6] = mt.getNanoTime();
		}
		return executeBatch(preparedInsertSql4MarketTrade(item), params);
	}
	
	public Long getCount(WatchListItem item) throws SQLException {
		ResultSetHandler<Object[]> handler = new ArrayHandler();
		Object[] resu = (Object[]) DbManager.inst().query("select count(*) from " + item.toMarketTradeTable(), handler);
		return (Long) resu[0];
	}
	
	public Long getMaxTradeTime(WatchListItem item) throws SQLException {
		Object[] resu = queryUnique("select max(trade_time) from " + item.toMarketTradeTable());
		return Utils.isEmpty(resu) ? null : (Long) resu[0];
	}
	
	public Long getMinTradeTime(WatchListItem item) throws SQLException {
		Object[] resu = queryUnique("select min(trade_time) from " + item.toMarketTradeTable());
		return Utils.isEmpty(resu) ? null : (Long) resu[0];
	}
	
	public MarketTrade findLatestTrade(WatchListItem item) throws SQLException {
		String sql = "select * from " + item.toMarketTradeTable() + " order by trade_time desc, trade_id desc, nano_time desc limit 1";
		return findUnique(sql);
	}
	
	public Long getCountByRange(WatchListItem item, long startMillis, long endMillis) throws SQLException {
		ResultSetHandler<Object[]> handler = new ArrayHandler();
		String sql = "select count(*) from " + item.toMarketTradeTable() + " where trade_time >= ? and trade_time < ?";
		
		Object[] resu = (Object[]) DbManager.inst().query(sql, handler, startMillis, endMillis);
		return Utils.isEmpty(resu) ? null : (Long) resu[0];
	}
	
	public static void main(String[] args) throws SQLException {
		WatchListItem item = new WatchListItem("bittrex", "URO", "BTC");
		MarketTradeService tser = new MarketTradeService();
//		System.out.println(tser.getCount(item));
//		System.out.println(tser.findTopTradeTimes(item, 200));
		item = new WatchListItem("cryptsy", "URO", "BTC");
		item = new WatchListItem("mintpal", "vrc", "btc");
		String sql = "select * from " + item.toMarketTradeTable() + " order by trade_time desc, trade_id desc limit 100";
		List<MarketTrade> mts = tser.find(sql);
		StringBuilder sb = new StringBuilder();
		for (MarketTrade mt : mts) {
//			sb.append(Bittrex.instance().reverseToJson(mt)).append("\n");
//			sb.append(Cryptsy.instance().reverseToJson(mt)).append("\n");
			sb.append(Mintpal.instance().reverseToJson(mt)).append("\n");
		}
		System.out.println(sb.toString());
	}
	
}

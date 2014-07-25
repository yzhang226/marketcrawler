package org.omega.marketcrawler.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public class MarketTradeService {
	
	public static final String OVERLAP_INSERT_SQL = "INSERT INTO overlap_market_trade (trade_time, trade_type, price, total_units, total_cost, item_id) VALUES (?,?,?,?,?,?)";
	private static final Map<String, String> columnToProperty = new HashMap<String, String>();
	
	static {
		columnToProperty.put("trade_time", "tradeTime");
		columnToProperty.put("trade_type", "tradeType");
		columnToProperty.put("total_units", "totalUnits");
		columnToProperty.put("total_cost", "totalCost");
	}
	
	protected String preparedInsertSql4MarketTrade(WatchListItem item) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(item.toMarketTradeTable()).append(" (")
		  .append("trade_time, trade_type, price, total_units, total_cost").append(") VALUES (?, ?, ?, ?, ?)")
		  .append(" ON DUPLICATE KEY UPDATE total_cost=VALUES(total_cost), total_units=VALUES(total_units), price=VALUES(price)");
		return sb.toString();
	}
	
	protected String preparedUpdateSql4MarketTrade(WatchListItem item) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(item.toMarketTradeTable()).append(" SET price = ?, total_units = ?, total_cost = ? where trade_time = ? and trade_type = ?");
		return sb.toString();
	}
	
	protected String createSql4MarketTrade(WatchListItem item) {
		StringBuilder create = new StringBuilder();
		create.append("CREATE TABLE ").append(item.toMarketTradeTable()).append(" ( ").append("\n")
		      .append("trade_time BIGINT NOT NULL , ").append("\n")
		      .append("trade_type TINYINT NOT NULL , ").append("\n")
		      .append("price DOUBLE NULL ,").append("\n")
		      .append("total_units DOUBLE NULL ,").append("\n")
		      .append("total_cost DOUBLE NULL ,").append("\n")
		      .append("PRIMARY KEY (trade_time, trade_type) );");
		
		return create.toString();
	}
	
	public boolean existWatchedTable(WatchListItem item) throws SQLException {
		return DbManager.inst().existTable(item.toMarketTradeTable());
	}
	
	public int createWatchedTable(WatchListItem item) throws SQLException {
		return DbManager.inst().execute(createSql4MarketTrade(item));
	}
	
	public boolean initWatchedTable(WatchListItem item) throws SQLException {
		if (!existWatchedTable(item)) {
			createWatchedTable(item);
			return true;
		}
		return false;
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
		return DbManager.inst().batch(preparedInsertSql4MarketTrade(item), params);
	}
	
	public int[] saveOverlapTrades(WatchListItem item, List<MarketTrade> records) throws SQLException {
		Object[][] params = new Object[records.size()][6];
		for (int i=0; i<records.size(); i++) {
			params[i][0] = records.get(i).getTradeTime();
			params[i][1] = records.get(i).getTradeType();
			params[i][2] = records.get(i).getPrice();
			params[i][3] = records.get(i).getTotalUnits();
			params[i][4] = records.get(i).getTotalCost();
			params[i][5] = item.getId();
		}
		return DbManager.inst().batch(OVERLAP_INSERT_SQL, params);
	}
	
	public int[] update(WatchListItem item, List<MarketTrade> records) throws SQLException {
		Object[][] params = new Object[records.size()][5];
		for (int i=0; i<records.size(); i++) {
			params[i][0] = records.get(i).getPrice();
			params[i][1] = records.get(i).getTotalUnits();
			params[i][2] = records.get(i).getTotalCost();
			params[i][3] = records.get(i).getTradeTime();
			params[i][4] = records.get(i).getTradeType();
		}
		return DbManager.inst().batch(preparedUpdateSql4MarketTrade(item), params);
	}
	
	public Long getCount(WatchListItem item) throws SQLException {
		ResultSetHandler<Object[]> handler = new ArrayHandler();
		Object[] resu = (Object[]) DbManager.inst().query("select count(*) from " + item.toMarketTradeTable(), handler);
		return (Long) resu[0];
	}
	
	public Long getMaxTradeTime(WatchListItem item) throws SQLException {
		Object[] resu = DbManager.inst().queryUnique("select max(trade_time) from " + item.toMarketTradeTable());
		return Utils.isEmpty(resu) ? null : (Long) resu[0];
	}
	
	public Long getMinTradeTime(WatchListItem item) throws SQLException {
		return (Long) DbManager.inst().queryUnique("select min(trade_time) from " + item.toMarketTradeTable())[0];
	}
	
	public List<Long> findTopTradeTimes(WatchListItem item, int limit) throws SQLException {
		String sql = new StringBuilder("select trade_time from ").append(item.toMarketTradeTable())
						.append(" order by trade_time desc").append(" limit ").append(limit).toString();
		ColumnListHandler<Long> handler = new ColumnListHandler<>(1);
		List<Long> resu = DbManager.inst().query(sql, handler);
		return resu;
	}
	
	public List<MarketTrade> findTopTrades(WatchListItem item, int limit) throws SQLException {
		String sql = new StringBuilder("select * from ").append(item.toMarketTradeTable())
						.append(" order by trade_time desc").append(" limit ").append(limit).toString();
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new BeanProcessor(columnToProperty));
		BeanListHandler<MarketTrade> handler = new BeanListHandler<>(MarketTrade.class, rowProcessor);
		List<MarketTrade> resu = DbManager.inst().query(sql, handler);
		return resu;
	}
	
	public List<MarketTrade> findOverlapMarketTrade(WatchListItem item, int limit) throws SQLException {
		String sql = "select * from overlap_market_trade where item_id = ? order by trade_time desc limit ?";
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new BeanProcessor(columnToProperty));
		BeanListHandler<MarketTrade> handler = new BeanListHandler<>(MarketTrade.class, rowProcessor);
		List<MarketTrade> resu = DbManager.inst().query(sql, handler, item.getId(), limit);
		return resu;
	}
	
	public Long getCountByRange(WatchListItem item, long startMillis, long endMillis) throws SQLException {
		ResultSetHandler<Object[]> handler = new ArrayHandler();
		String sql = "select count(*) from " + item.toMarketTradeTable() + " where trade_time >= ? and trade_time < ?";
		
		Object[] resu = (Object[]) DbManager.inst().query(sql, handler, startMillis, endMillis);
		return Utils.isEmpty(resu) ? null : (Long) resu[0];
	}
	
	
	public static void main(String[] args) throws SQLException {
		WatchListItem item = new WatchListItem("mintpal", "VRC", Symbol.BTC.name());
		MarketTradeService tser = new MarketTradeService();
//		System.out.println(tser.getCount(item));
		System.out.println(tser.findTopTradeTimes(item, 200));
	}
	
}

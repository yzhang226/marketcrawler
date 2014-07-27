package org.omega.marketcrawler.service;

import static org.omega.marketcrawler.entity.MarketTrade.TRADE_TYPE_BUY;
import static org.omega.marketcrawler.entity.MarketTrade.TRADE_TYPE_SELL;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.DbManager;
import org.omega.marketcrawler.entity.TradeStatistics;
import org.omega.marketcrawler.entity.WatchListItem;

public class TradeStatisticsService  extends SimpleDBService<TradeStatistics> {

	private static final String table = "trade_statistics_one_minute";
	
	private static final String INSERT_SQL = "INSERT INTO " + table +" (item_id,start_time,end_time,open,high,low,close,watched_vol,exchange_vol,count,"
			+ "buy_watched_vol,buy_exchange_vol,buy_avg_price,buy_count,sell_watched_vol,sell_exchange_vol,sell_avg_price,sell_count) "
			+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
			+ " ON DUPLICATE KEY UPDATE open=VALUES(open), high=VALUES(high), low=VALUES(low), close=VALUES(close), "
			+ " watched_vol=VALUES(watched_vol), exchange_vol=VALUES(exchange_vol), count=VALUES(count), "
			+ " buy_watched_vol=VALUES(buy_watched_vol), buy_exchange_vol=VALUES(buy_exchange_vol), buy_avg_price=VALUES(buy_avg_price), buy_count=VALUES(buy_count), "
			+ " sell_watched_vol=VALUES(sell_watched_vol), sell_exchange_vol=VALUES(sell_exchange_vol), sell_avg_price=VALUES(sell_avg_price), sell_count=VALUES(sell_count)";
	
	static {
		columnToProperty.put("item_id", "itemId");
		columnToProperty.put("start_time", "startTime");
		columnToProperty.put("end_time", "endTime");
		columnToProperty.put("watched_vol", "watchedVol");
		columnToProperty.put("exchange_vol", "exchangeVol");
		
		columnToProperty.put("buy_watched_vol", "buyWatchedVol");
		columnToProperty.put("buy_exchange_vol", "buyExchangeVol");
		columnToProperty.put("buy_avg_price", "buyAvgPrice");
		columnToProperty.put("buy_count", "buyCount");
		
		columnToProperty.put("sell_watched_vol", "sellWatchedVol");
		columnToProperty.put("sell_exchange_vol", "sellExchangeVol");
		columnToProperty.put("sell_avg_price", "sellAvgPrice");
		columnToProperty.put("sell_count", "sellCount");
	}
	
	protected Map<String, String> getColumnToProperty() {
		return columnToProperty;
	}
	
	protected String getTableName() {
		return table;
	}
	
	private Object[] convertBeanPropertiesToArray(TradeStatistics stat) {
		return new Object[]{stat.getItemId(), stat.getStartTime(), stat.getEndTime(), stat.getOpen(), stat.getHigh(), stat.getLow(), stat.getClose(), 
						    stat.getWatchedVol(), stat.getExchangeVol(), stat.getCount()};
	}
	
	public int[] saveOneMinuteStatistics(List<TradeStatistics> stats) throws SQLException {
		Object[][] params = new Object[stats.size()][9];
		for (int i=0; i<stats.size(); i++) {
			params[i] = convertBeanPropertiesToArray(stats.get(i));
		}
		return executeBatch(INSERT_SQL, params);
	}
	
	public Integer getMaxStartTime(int itemId) throws SQLException {
		String sql = "select max(start_time) from " + table + " where item_id = ?";
		Object[] resu = DbManager.inst().queryUnique(sql, itemId);
		return Utils.isEmpty(resu) ? null : (Integer) resu[0];
	}
	
	public TradeStatistics getByIdAndTime(int itemId, int startSecs, int endSecs) throws SQLException {
		String sql = "select * from " + table + " where item_id = ? and start_time = ? and end_time = ?";
		return findUnique(sql, itemId, startSecs, endSecs);
	}
	
	public int[] doOneMinuteStatistics(WatchListItem item , int minutes) throws SQLException {
		MarketTradeService mtser = new MarketTradeService();
		TradeStatisticsService statSer = new TradeStatisticsService();
		
		int[] resu = null;
		Float open = null, close = null;
		Object[] arr = null;
		List<Object[]> buySellGroups = null;
		String table = item.toMarketTradeTable();
		
		String statisticsSql = new StringBuilder("select max(price) as high, min(price) as low")
								.append(", sum(total_units) as watchedVolume, sum(total_cost) as exchangeVolume, count(*) as tradeCount from ")
								.append(table).append(" where trade_time >= ? and trade_time < ?").toString();
		
		String buySellSql = new StringBuilder("select trade_type, sum(total_units), sum(total_cost) , ")
								.append("cast(sum(total_cost)/sum(total_units) as dec(16,8)), ")
								.append("count(trade_type) ").append("from ").append(table)
								.append(" where trade_time >= ? and trade_time < ? group by trade_type order by trade_type asc").toString();
		
		String openSql = new StringBuilder("select price from ").append(table).append(" where trade_time >= ? and trade_time < ? order by trade_time asc limit 1").toString();
		
		String closeSql = new StringBuilder("select price from ").append(table).append(" where trade_time >= ? and trade_time < ? order by trade_time desc limit 1").toString();
		
		TradeStatistics stat = null;
		List<TradeStatistics> stats = new ArrayList<>(minutes);
		Long max = mtser.getMaxTradeTime(item);
		long maxRangeStart = Utils.getOneMinuteRangeStart(max);
		long maxRangeEnd = Utils.getOneMinuteRangeEnd(max);
		long rangeStart, rangeEnd;
		for (int i=0; i<minutes; i++) {
			rangeStart = maxRangeStart - i * Constants.MILLIS_ONE_MINUTE;
			rangeEnd = maxRangeEnd - i * Constants.MILLIS_ONE_MINUTE;
			arr = DbManager.inst().queryUnique(openSql, rangeStart, rangeEnd);
			if (arr != null && arr[0] != null) {
				open = (Float) arr[0];
				close = (Float) queryUnique(closeSql, rangeStart, rangeEnd)[0];
				arr = queryUnique(statisticsSql, rangeStart, rangeEnd);
				buySellGroups = query(buySellSql, rangeStart, rangeEnd);
				
				stat = new TradeStatistics();
				stat.setItemId((short) item.getId());
				stat.setStartTime((int) (rangeStart/100));// seconds
				stat.setEndTime((int) (rangeEnd/100));// seconds
				stat.setOpen(open);
				stat.setClose(close);
				stat.setHigh((Float) arr[0]);
				stat.setLow((Float) arr[1]);
				stat.setWatchedVol((Float) arr[2]);
				stat.setExchangeVol((Float) arr[3]);
				stat.setCount(((Long) arr[4]).shortValue());
				
				statBuySell(buySellGroups, stat);
				
				stats.add(stat);
			}
		}
		
		resu = statSer.saveOneMinuteStatistics(stats);
		
		return resu;
	}
	
	private void statBuySell(List<Object[]> buySellGroups, TradeStatistics stat) {
		if (Utils.isEmpty(buySellGroups)) return;
		Map<Byte, Object[]> map = new HashMap<>(2);
		byte tradeType;
		for (Object[] arr : buySellGroups) {
			tradeType = (Byte) arr[0];
			if (tradeType == TRADE_TYPE_BUY) {
				map.put(TRADE_TYPE_BUY, arr);
			} else if (tradeType == TRADE_TYPE_SELL) {
				map.put(TRADE_TYPE_SELL, arr);
			}
		}
		
		/*  trade_type, sum(total_units), sum(total_cost) , cast(sum(total_cost)/sum(total_units) as dec(16,8)), count(trade_type) */
		Object[] buy = map.get(TRADE_TYPE_BUY), sell = map.get(TRADE_TYPE_SELL);
		if (buy != null) {
			stat.setBuyWatchedVol((Float) buy[1]);
			stat.setBuyExchangeVol((Float) buy[2]);
			stat.setBuyAvgPrice(((BigDecimal) buy[3]).floatValue());
			stat.setBuyCount(((Long) buy[4]).shortValue());
		}
		if (sell != null) {
			stat.setSellWatchedVol((Float) sell[1]);
			stat.setSellExchangeVol((Float) sell[2]);
			stat.setSellAvgPrice(((BigDecimal) sell[3]).floatValue());
			stat.setSellCount(((Long) sell[4]).shortValue());
		}
		
	}
	
	public int[] initStatistics(WatchListItem item) throws SQLException {
		MarketTradeService mtser = new MarketTradeService();
		Long max = mtser.getMaxTradeTime(item);
		
		if (max == null) { return null; }
		
		Long min = mtser.getMinTradeTime(item);
		
		max = Utils.getOneMinuteRangeEnd(max);
		min = Utils.getOneMinuteRangeEnd(min);
		
		int minutes = (int) ((max - min) / Constants.MILLIS_ONE_MINUTE);
		
		return doOneMinuteStatistics(item, minutes);
	}
	
	public static void main(String[] args) throws SQLException {
//		WatchListItem item = new WatchListItem(Mintpal.NAME, "URO", Symbol.BTC.name());
		TradeStatisticsService statSer = new TradeStatisticsService();
		WatchListItemService wiser = new WatchListItemService();
		List<WatchListItem> items = wiser.findAll();
		for (WatchListItem item : items) {
//			if (item.getId() == 20) {
				statSer.initStatistics(item);
//			}
			
//			break;
		}
		
	}
	
}

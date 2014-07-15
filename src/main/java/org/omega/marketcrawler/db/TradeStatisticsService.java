package org.omega.marketcrawler.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.TradeStatistics;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.operator.Mintpal;

public class TradeStatisticsService {

	private static final Log log = LogFactory.getLog(TradeStatisticsService.class);
	
	private static final String INSERT_SQL = "INSERT INTO trade_statistics_one_minute (item_id,start_time,end_time,open,high,low,close,watched_vol,exchange_vol,count)  "
			+ " VALUES (?,?,?,?,?,?,?,?,?,?)"
			+ " ON DUPLICATE KEY UPDATE open=VALUES(open), high=VALUES(high), low=VALUES(low), close=VALUES(close), "
			+ " watched_vol=VALUES(watched_vol), exchange_vol=VALUES(exchange_vol), count=VALUES(count)";
	
	
	private Object[] convertBeanPropertiesToArray(TradeStatistics stat) {
		return new Object[]{stat.getItemId(), stat.getStartTime(), stat.getEndTime(), stat.getOpen(), stat.getHigh(), stat.getLow(), stat.getClose(), 
						    stat.getWatchedVol(), stat.getExchangeVol(), stat.getCount()};
	}
	
	// 
	public int[] saveOneMinuteStatistics(List<TradeStatistics> stats) throws SQLException {
		Object[][] params = new Object[stats.size()][9];
		for (int i=0; i<stats.size(); i++) {
			params[i] = convertBeanPropertiesToArray(stats.get(i));
		}
		
		return DbManager.inst().batch(INSERT_SQL, params);
	}
	
	public int[] doOneMinuteStatistics(WatchListItem item , int minutes) throws SQLException {
		MarketTradeService mtser = new MarketTradeService();
		TradeStatisticsService statSer = new TradeStatisticsService();
		
		int[] resu = null;
		Double open = null, close = null;
		Object[] arr = null;
		String table = item.toMarketTradeTable();
		
		String statisticsSql = new StringBuilder("select max(price) as high, min(price) as low")
								.append(", sum(total_units) as watchedVolume, max(total_cost) as exchangeVolume, count(*) as tradeCount from ")
								.append(table).append(" where trade_time > ? and trade_time < ?").toString();
		String openSql = new StringBuilder("select price from ").append(table).append(" where trade_time > ? and trade_time < ? order by trade_time asc limit 1").toString();
		String closeSql = new StringBuilder("select price from ").append(table).append(" where trade_time > ? and trade_time < ? order by trade_time desc limit 1").toString();
		
		TradeStatistics stat = null;
		List<TradeStatistics> stats = new ArrayList<>(minutes);
		Long max = mtser.getMaxTradeTime(item);
		long maxRangeStart = Utils.getOneMinuteRangeStart(max);
		long maxRangeEnd = Utils.getOneMinuteRangeEnd(max);
		long rangeStart, rangeEnd;
		// int emptyIdx = 0;
		for (int i=0; i<minutes; i++) {
			rangeStart = maxRangeStart - i * Constants.MILLIS_ONE_MINUTE;
			rangeEnd = maxRangeEnd - i * Constants.MILLIS_ONE_MINUTE;
			arr = DbManager.inst().queryUnique(openSql, rangeStart, rangeEnd);
			if (arr != null && arr[0] != null) {
				open = (Double) arr[0];
				close = (Double) DbManager.inst().queryUnique(closeSql, rangeStart, rangeEnd)[0];
				arr = DbManager.inst().queryUnique(statisticsSql, rangeStart, rangeEnd);
				
				stat = new TradeStatistics();
				stat.setItemId(item.getId());
				stat.setStartTime(rangeStart);
				stat.setEndTime(rangeEnd);
				stat.setOpen(open);
				stat.setClose(close);
				stat.setHigh((Double) arr[0]);
				stat.setLow((Double) arr[1]);
				stat.setWatchedVol((Double) arr[2]);
				stat.setExchangeVol((Double) arr[3]);
				stat.setCount(((Long) arr[4]).intValue());
				
				stats.add(stat);
			} else {
				// emptyIdx++;
				// System.out.println("emptyIdx: " + emptyIdx + ", " + rangeStart + ", " +  rangeEnd);
			}
		}
		
		resu = statSer.saveOneMinuteStatistics(stats);
		
		return resu;
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

package org.omega.marketcrawler.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.operator.Bittrex;
import org.omega.marketcrawler.operator.Mintpal;
import org.omega.marketcrawler.operator.Poloniex;

public class MarketSummaryService {
	
//	private static final Log log = LogFactory.getLog(MarketSummaryService.class);
	
	private static final Map<String, String> columnToProperty = new HashMap<String, String>();
	
	static {
		columnToProperty.put("watched_symbol", "watchedSymbol");
		columnToProperty.put("exchange_symbol", "exchangeSymbol");
		columnToProperty.put("market_id", "marketId");
		columnToProperty.put("watched_coin_name", "watchedCoinName");
		columnToProperty.put("exchange_coin_name", "exchangeCoinName");
		columnToProperty.put("last_price", "lastPrice");
		columnToProperty.put("yesterday_price", "yesterdayPrice");
		columnToProperty.put("coin_volume24h", "coinVolume24h");
		columnToProperty.put("top_bid", "topBid");
		columnToProperty.put("top_ask", "topAsk");
		columnToProperty.put("update_time", "updateTime");
	}
	
	private Object[] convertBeanPropertiesToArray(MarketSummary summ) {
		return new Object[]{summ.getOperator(), summ.getWatchedSymbol(), summ.getExchangeSymbol(), summ.getMarketId(), summ.getWatchedCoinName(), 
				summ.getExchangeCoinName(), summ.getLastPrice(), 
				summ.getYesterdayPrice(), summ.getFluctuation(), summ.getHighest24h(), summ.getLowest24h(), 
				summ.getVolume24h(), summ.getCoinVolume24h(), summ.getTopBid(), summ.getTopAsk(), summ.getUpdateTime()};
	}
	
	// 
	public int[] save(List<MarketSummary> summs) throws SQLException {
		if (Utils.isEmpty(summs)) return new int[]{0};
		StringBuilder sql = new StringBuilder("INSERT INTO market_summary ");
		sql.append("(operator,watched_symbol,exchange_symbol,market_id,watched_coin_name,exchange_coin_name,last_price,yesterday_price,fluctuation,highest24h,lowest24h,volume24h,coin_volume24h,top_bid,top_ask,update_time) ");
		sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		sql.append(" ON DUPLICATE KEY UPDATE last_price=VALUES(last_price), yesterday_price=VALUES(yesterday_price), fluctuation=VALUES(fluctuation), highest24h=VALUES(highest24h), ");
		sql.append(" lowest24h=VALUES(lowest24h), volume24h=VALUES(volume24h), coin_volume24h=VALUES(coin_volume24h), top_bid=VALUES(top_bid), top_ask=VALUES(top_ask), update_time=VALUES(update_time)");
		
		Object[][] params = new Object[summs.size()][14];
		for (int i=0; i<summs.size(); i++) {
			params[i] = convertBeanPropertiesToArray(summs.get(i));
		}
		
		return DbManager.inst().batch(sql.toString(), params);
	}
	
	public List<MarketSummary> findAll() throws SQLException {
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new BeanProcessor(columnToProperty));
		BeanListHandler<MarketSummary> handler = new BeanListHandler<>(MarketSummary.class, rowProcessor);
		
		return DbManager.inst().query("select * from market_summary",  handler);
	}
	
	public List<WatchListItem> findWatchedItems(List<String> watchedSymbols) throws SQLException {
		if (Utils.isEmpty(watchedSymbols)) return new ArrayList<>(0);
		
		StringBuilder nsql = new StringBuilder("select operator, watched_symbol, exchange_symbol, market_id from market_summary where watched_symbol in (");
		for (String sym : watchedSymbols) {
			nsql.append("'").append(sym).append("',");
		}
		nsql.deleteCharAt(nsql.length() - 1);
		nsql.append(")");
		
		// TODO:
		
//		System.out.println(nsql.toString());
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new BeanProcessor(columnToProperty));
		BeanListHandler<WatchListItem> handler = new BeanListHandler<>(WatchListItem.class, rowProcessor);
		
		return DbManager.inst().query(nsql.toString(), handler);
	}
	
	public void refreshAllSummaries() throws Exception {
		save(Mintpal.instance().getMarketSummaries());
		save(Bittrex.instance().getMarketSummaries());
		save(Poloniex.instance().getMarketSummaries());
	}
	
	public static void main(String[] args) throws SQLException {
		MarketSummaryService ser = new MarketSummaryService();
//		ser.save(Mintpal.instance().getMarketSummaries());
//		ser.save(Bittrex.instance().getMarketSummaries());
		
		List<MarketSummary> summs = ser.findAll();
		for (MarketSummary summ : summs) {
			System.out.println(summ.toReadableText());
		}
		
	}
	
}

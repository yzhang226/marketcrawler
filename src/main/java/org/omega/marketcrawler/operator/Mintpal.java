package org.omega.marketcrawler.operator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Arith;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public final class Mintpal extends TradeOperator {
	
	private static final Log log = LogFactory.getLog(Mintpal.class);
	
	public static final String NAME = "mintpal";
	private static final String VERSION = "v2";
	
//	private static final int DEFAULT_LIMITATION = 50;
	
	public static final String STATUS_SUCCESS = "success";
	
	public static final String KEY_STATUS = "status";
	public static final String KEY_COUNT = "count";
	public static final String KEY_DATA = "data";
	
	private static final Mintpal inst = new Mintpal();
	private Mintpal() {}
	
	public static Mintpal instance() {
		return inst;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	public String getBaseAPI() {
		return new StringBuilder("https://api.mintpal.com/").append(VERSION).append("/").toString();
	}

	// https://api.mintpal.com/v2/market/summary/{EXCHANGE}
	public String getMarketSummaryAPI() {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("market/summary");
		return api.toString();
	}
	
	// https://api.mintpal.com/v2/market/trades/{COIN}/{EXCHANGE} maximum limit is 200.
	// https://api.mintpal.com/v2/market/trades/{COIN}/{EXCHANGE}/{LIMIT} - 404, not implement 
	public String getMarketTradeAPI(WatchListItem item) {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("market/trades/").append(item.getWatchedSymbol()).append("/").append(item.getExchangeSymbol());
		return api.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<MarketSummary> transferJsonToMarketSummary(Object jsonObj) {
		LinkedHashMap<String, Object> json = (LinkedHashMap<String, Object>) jsonObj;
		List<MarketSummary> records = null;
		if (STATUS_SUCCESS.equals(String.valueOf(json.get(KEY_STATUS)))) {
			int count = 20;
			if (json.containsKey(KEY_COUNT)) {
				count = Integer.valueOf(String.valueOf(json.get(KEY_COUNT)));
			}
			records = new ArrayList<>(count);
			List<Map<String, String>> data = (List<Map<String, String>>) json.get(KEY_DATA);
			MarketSummary summ = null;
			Timestamp curr = new Timestamp(System.currentTimeMillis());
			for (Map<String, String> da : data) {
				summ = new MarketSummary();
				try {
					summ.setOperator(NAME);
					summ.setMarketId(Integer.valueOf(da.get("market_id")));
					if (da.containsKey("coin")) summ.setWatchedCoinName(da.get("coin"));
					summ.setWatchedSymbol(da.get("code"));
					summ.setExchangeSymbol(da.get("exchange"));
					summ.setLastPrice(Double.valueOf(da.get("last_price")));
					summ.setYesterdayPrice(Double.valueOf(da.get("yesterday_price")));
					summ.setFluctuation(Double.valueOf(da.get("change")));
					summ.setHighest24h(Double.valueOf(da.get("24hhigh")));
					summ.setLowest24h(Double.valueOf(da.get("24hlow")));
					summ.setVolume24h(Double.valueOf(da.get("24hvol")));
					summ.setTopBid(Double.valueOf(da.get("top_bid")));
					summ.setTopAsk(Double.valueOf(da.get("top_ask")));
					summ.setUpdateTime(curr);
				} catch (Exception e) {
					summ = null;
					log.error("", e);
				}
				if (summ != null) { records.add(summ); }
			}
		}
		return records;
	}
	
	@SuppressWarnings("unchecked")
	public List<MarketTrade> transferJsonToMarketTrade(Object jsonObj) {
		LinkedHashMap<String, Object> json = (LinkedHashMap<String, Object>) jsonObj;
		List<MarketTrade> records = null;
		if (STATUS_SUCCESS.equals(String.valueOf(json.get(KEY_STATUS)))) {
			int count = 20;
			if (json.containsKey(KEY_COUNT)) {
				count = Integer.valueOf(String.valueOf(json.get(KEY_COUNT)));
			}
			records = new ArrayList<>(count);
			List<Map<String, String>> data = (List<Map<String, String>>) json.get(KEY_DATA);
			MarketTrade re = null;
			String field = null;
			for (Map<String, String> da : data) {
				re = new MarketTrade();
				try {
					if ((field = da.get("type")) != null) {
						re.setTradeType(MarketTrade.parseTradeType(field));
					}
					if ((field = da.get("price")) != null) { re.setPrice(Double.valueOf(field)); } 
					if ((field = da.get("amount")) != null) { re.setTotalUnits(Double.valueOf(field)); }
					if ((field = da.get("total")) != null) { re.setTotalCost(Double.valueOf(field)); }
					if ((field = da.get("time")) != null) {
						/* NOTE: Time is specified as a unix timestamp with microseconds.
						 * microsecond μs 1 microsecond = 1,000 nanoseconds
						 * millisecond ms 1 millisecond = 1,000 microseconds  */
						re.setTradeTime((long) Arith.multiply(Double.valueOf(field), 1000));
					}
				} catch (Exception e) {
					re = null;
					log.error("convert one json row error.", e);
				}
				if (re != null) records.add(re);
			}
		}
		return records;
	}
	
	public static void main(String[] args) {
//		Mintpal.instance().getHistory("DOGE", Symbol.BTC.name());
		String watchedSymbol = "BC";
		String exchangeSymbol = Symbol.BTC.name();
//		List<MarketTrade> records = Mintpal.instance().getMarketTrades(watchedSymbol, exchangeSymbol);
//		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ss");
//		System.out.println("records.size() is  " + records.size());
//		for (MarketTrade r : records) {
//			System.out.println(r.toReadableText());
//		}
//		System.out.println(SqlUtils.getInsertSql4TradeRecord(watchedSymbol, exchangeSymbol));
		
		List<MarketSummary> summs = Mintpal.instance().getMarketSummaries();
		for (MarketSummary summ : summs) {
			System.out.println(summ.toReadableText());
		}
		
	}

}

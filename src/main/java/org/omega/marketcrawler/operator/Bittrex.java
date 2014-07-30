package org.omega.marketcrawler.operator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.net.MultiThreadedNetter;

public final class Bittrex extends Operator {
	
	private static final Log log = LogFactory.getLog(Bittrex.class);
	
	private static final Bittrex inst = new Bittrex();
	
	public static final String NAME = "bittrex";
	private static final String VERSION = "v1.1";
	
	// count	optional	a number between 1-100 for the number of entries to return (default = 20)
	public static final int DEFAULT_LIMIT = 100;
	
	public static final String STATUS_TRUE = "true";
	
	public static final String KEY_SUCCESS = "success";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_RESULT = "result";
	
	public static final String TIME_PATTERN_BITTREX = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	private Bittrex() {}
	
	public static Bittrex instance() {
		return inst;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	public String getBaseAPI() {
		return new StringBuilder("https://bittrex.com/api/").append(VERSION).append("/").toString();
	}

	// https://bittrex.com/api/v1/public/getmarketsummaries
	public String getMarketSummaryAPI() {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("public/getmarketsummaries");
		return api.toString();
	}
	
	// https://bittrex.com/api/v1/public/getmarkethistory?market=BTC-DOGE&count=5
	public String getMarketTradeAPI(WatchListItem item) {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("public/getmarkethistory?market=").append(item.getExchangeSymbol()).append("-").append(item.getWatchedSymbol());
		api.append("&count=").append(DEFAULT_LIMIT);
		return api.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<MarketSummary> transferJsonToMarketSummary(Object jsonObj) {
		LinkedHashMap<String, Object> json = (LinkedHashMap<String, Object>) jsonObj;
		List<MarketSummary> records = null;
		
		if (STATUS_TRUE.equals(String.valueOf(json.get(KEY_SUCCESS)))) {
			records = new ArrayList<>(45);
//			SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN_BITTREX);
			DateTimeFormatter formatter = DateTimeFormat.forPattern(TIME_PATTERN_BITTREX);
			List<Map<String, Object>> data = (List<Map<String, Object>>) json.get(KEY_RESULT);
			MarketSummary summ = null;
			Object field = null;
			for (Map<String, Object> da : data) {
				summ = new MarketSummary();
				try {
					summ.setOperator(NAME);
					String[] ss = da.get("MarketName").toString().split("-");
					summ.setWatchedSymbol(ss[1]);
					summ.setExchangeSymbol(ss[0]);
					
					if ((field = da.get("Last")) != null) summ.setLastPrice((Double) field);
					if ((field = da.get("PrevDay")) != null) summ.setYesterdayPrice((Double) field);
					if ((field = da.get("High")) != null) summ.setHighest24h((Double) field);
					if ((field = da.get("Low")) != null) summ.setLowest24h((Double) field);
					if ((field = da.get("BaseVolume")) != null) summ.setVolume24h((Double) field);
					if ((field = da.get("Volume")) != null) summ.setCoinVolume24h((Double) field);
					if ((field = da.get("Bid")) != null) summ.setTopBid((Double) field);
					if ((field = da.get("Ask")) != null) summ.setTopAsk((Double) field);
					if ((field = da.get("TimeStamp")) != null) {// "2014-04-19T20:49:50.053" 2014-07-07T05:16:07
						long millonSec = parseMillsecs((String) field, formatter);
						summ.setUpdateTime(new Timestamp(millonSec));
					}
				} catch (Exception e) {
					summ = null;
					log.error("convert one json row error.", e);
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
		if (STATUS_TRUE.equals(String.valueOf(json.get(KEY_SUCCESS)))) {
			records = new ArrayList<>(25);
//			SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN_BITTREX);
			DateTimeFormatter formatter = DateTimeFormat.forPattern(TIME_PATTERN_BITTREX);
			List<Map<String, Object>> data = (List<Map<String, Object>>) json.get(KEY_RESULT);
			MarketTrade re = null;
			Object fieldValue = null;
			for (Map<String, Object> da : data) {
				re = new MarketTrade();
				try {
					/* {"Id":111910,"TimeStamp":"2014-07-26T12:51:14.477","Quantity":50.00000000,"Price":0.00069000,"Total":0.03450000,"FillType":"FILL","OrderType":"SELL"},
					 * {"Id":111909,"TimeStamp":"2014-07-26T12:51:14.477","Quantity":88.85123887,"Price":0.00069069,"Total":0.06136866,"FillType":"FILL","OrderType":"SELL"} */
					if ((fieldValue = da.get("Id")) != null) { re.setTradeId((Integer) fieldValue); }
					if ((fieldValue = da.get("Price")) != null) { re.setPrice(((Double) fieldValue)); }
					if ((fieldValue = da.get("Quantity")) != null) { re.setTotalUnits(((Double) fieldValue)); }
					if ((fieldValue = da.get("Total")) != null) { re.setTotalCost(((Double) fieldValue)); }
					if ((fieldValue = da.get("TimeStamp")) != null) {  re.setTradeTime(parseMillsecs((String) fieldValue, formatter)); }
					if ((fieldValue = da.get("OrderType")) != null) { re.setTradeType(MarketTrade.parseTradeType((String) fieldValue)); }
					
					records.add(re);
				} catch (Exception e) {
					log.error("convert one json row error.", e);
				}
			}
		}
		
		return records;
	}
	
	public String reverseToJson(MarketTrade mt) {
		// {"Id":111910,"TimeStamp":"2014-07-26T12:51:14.477","Quantity":50.00000000,"Price":0.00069000,"Total":0.03450000,"FillType":"FILL","OrderType":"SELL"}
//		SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN_BITTREX);
		DateTimeFormatter formatter = DateTimeFormat.forPattern(TIME_PATTERN_BITTREX);
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"").append("Id").append("\"").append(":").append(mt.getTradeId()).append(",")
		  .append("\"").append("TimeStamp").append("\"").append(":").append("\"").append(formatter.print(mt.getTradeTime())).append("\"").append(",")
		  .append("\"").append("Quantity").append("\"").append(":").append(Utils.right8Pad(mt.getTotalUnits())).append(",")
		  .append("\"").append("Price").append("\"").append(":").append(Utils.right8Pad(mt.getPrice())).append(",")
		  .append("\"").append("Total").append("\"").append(":").append(Utils.right8Pad(mt.getTotalCost())).append(",")
		  .append("\"").append("FillType").append("\"").append(":").append("\"").append("FILL").append("\"").append(",")
		  .append("\"").append("OrderType").append("\"").append(":").append("\"").append(MarketTrade.formatTradeType(mt.getTradeType()).toUpperCase()).append("\"");
		sb.append("}");
		return sb.toString();
	}
	

	public long parseMillsecs(String time, DateTimeFormatter formatter) {
		long millsec = 0;
		try {
			if (Utils.isNotEmpty(time) && time.length() < 20) {
				time = time + ".000";
			}
			if (Utils.isNotEmpty(time)) millsec = formatter.parseMillis(time);
		} catch (Exception e) {
			log.error("parse date text[" + time + "] error.", e);
		}

		return millsec;
	}
	
	public static void main(String[] args) throws Exception {
//		Mintpal.instance().getHistory("DOGE", Symbol.BTC.name());
//		String watchedSymbol = "URO";
//		String exchangeSymbol = Symbol.BTC.name();
//		WatchListItem item = new WatchListItem(NAME, "URO", Symbol.BTC.name());
//		System.out.println(inst.getMarketTradeAPI(item));
//		List<MarketTrade> records = Bittrex.instance().getMarketTrades(item);
//		MarketTradeService tser = new MarketTradeService();
//		tser.save(item, records);
//		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		for (TradeRecord r : records) {
//			System.out.println(sd.format(new Date(r.getTradeTime())) + ", " + r.toReadableText());
//		}
		
//		List<MarketSummary> summs = Bittrex.instance().getMarketSummaries();
//		for (MarketSummary summ : summs) {
//			System.out.println(summ.toReadableText());
//		}
//		MultiThreadedNetter netter = MultiThreadedNetter.inst();
//		netter.reinit();
//		System.out.println(inst.getMarketTradeAPI(item));
//		System.out.println(inst.getMarketTrades(item));
//		netter.close();
		
		WatchListItem item = new WatchListItem("bittrex", "vrc", "BTC");
		System.out.println(inst.getMarketTradeAPI(item));
		
	}

}

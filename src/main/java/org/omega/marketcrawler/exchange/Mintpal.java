package org.omega.marketcrawler.exchange;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Arith;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.TradeRecord;
import org.omega.marketcrawler.net.NetUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Mintpal extends TradeOperator {
	
	private static final Log log = LogFactory.getLog(Mintpal.class);
	
	public static final String NAME = "mintpal";
	private static final String VERSION = "v2";
	
	private static final int DEFAULT_LIMITATION = 100;
	
	public static final String STATUS_SUCCESS = "success";
	public static final String TYPE_SELL = "SELL";
	public static final String TYPE_BUY = "BUY";
	
	public static final String KEY_STATUS = "status";
	public static final String KEY_COUNT = "count";
	public static final String KEY_DATA = "data";
	
	private static final Mintpal inst = new Mintpal();
	private Mintpal() {}
	
	public static Mintpal instance() {
		return inst;
	}
	
	public String getBaseAPI() {
		return new StringBuilder("https://api.mintpal.com/").append(VERSION).append("/").toString();
	}
	
	// https://api.mintpal.com/v2/market/trades/{COIN}/{EXCHANGE}
	public String getMarketTradeAPI(String watchedSymbol, String exchangeSymbol) {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("market/trades/").append(watchedSymbol).append("/").append(exchangeSymbol);
		return api.toString();
	}
	
	// https://api.mintpal.com/v2/market/summary/{EXCHANGE}
	public String getMarketSummaryAPI(String... exchangeSymbol) {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("market/summary/");
		if (Utils.isNotEmpty(exchangeSymbol)) {
			api.append(exchangeSymbol[0]);
		}
		return api.toString();
	}
	
	
	public String getHistoryJsonText(String watchedSymbol, String exchangeSymbol) {
		return NetUtils.accessDirectly(getMarketTradeAPI(watchedSymbol, exchangeSymbol));
	}
	
	@SuppressWarnings("unchecked")
	public List<MarketSummary> getMarketSummarys(String... exchangeSymbol) {
		List<MarketSummary> records = null;
		try {
			String recordText = NetUtils.accessDirectly(getMarketSummaryAPI(exchangeSymbol));
			ObjectMapper mapper = new ObjectMapper();
			
			LinkedHashMap<String, Object> map = mapper.readValue(recordText, LinkedHashMap.class);
			
			if (STATUS_SUCCESS.equals(String.valueOf(map.get(KEY_STATUS)))) {
				int count = 20;
				if (map.containsKey(KEY_COUNT)) {
					count = Integer.valueOf(String.valueOf(map.get(KEY_COUNT)));
				}
				records = new ArrayList<>(count);
				List<Map<String, String>> data = (List<Map<String, String>>) map.get(KEY_DATA);
				MarketSummary summ = null;
				for (Map<String, String> da : data) {
					summ = transfer(da);
					if (summ != null) {
						records.add(summ);
					}
				}
			}
			
		} catch (Exception e) {
			log.error("try to get and convert json Market Summary to object error.", e);
		}
		
		return records;
	}
	
	private MarketSummary transfer(Map<String, String> da) {
		MarketSummary summ = new MarketSummary();
		
		summ.setMarketId(Integer.valueOf(da.get("market_id")));
		if (da.containsKey("coin")) summ.setCoinName(da.get("coin"));
		summ.setWatchedSymbol(da.get("code"));
		summ.setExchangeSymbol(da.get("exchange"));
		summ.setLastPrice(Double.valueOf(da.get("last_price")));
		summ.setYesterdayPrice(Double.valueOf(da.get("yesterday_price")));
		summ.setChange(Double.valueOf(da.get("exchange")));
		summ.setHighest24h(Double.valueOf(da.get("24hhigh")));
		summ.setLowest24h(Double.valueOf(da.get("24hlow")));
		summ.setVolume24h(Double.valueOf(da.get("24hlow")));
		summ.setTopBid(Double.valueOf(da.get("top_bid")));
		summ.setTopAsk(Double.valueOf(da.get("top_ask")));
		
		return summ;
	}

	/**
	 * NOTE: Type 0 refers to a BUY and type 1 refers to a SELL. Time is specified as a unix timestamp with microseconds.
	 */
	public List<TradeRecord> getHistory(String watchedSymbol, String exchangeSymbol) {
		List<TradeRecord> records = null;
		try {
			String recordText = getHistoryJsonText(watchedSymbol, exchangeSymbol);
			JsonFactory jfactory = new JsonFactory();
			JsonParser parser = jfactory.createParser(recordText);
			records = readMarketTrades(parser);
		} catch (Exception e) {
			log.error("try to get and convert json history to object error.", e);
		}
		
		return records;
	}
	
	private List<TradeRecord> readMarketTrades(JsonParser parser) throws Exception {
	  if (parser.nextToken() != JsonToken.START_OBJECT) {
	    throw new Exception("Expected data to start with an Object");
	  }

	  List<TradeRecord> records = null;
	  int count = 0;
	  String fieldValue = null;
	  
	  while (parser.nextToken() != JsonToken.END_OBJECT) {
	   String fieldName = parser.getCurrentName();
	   parser.nextToken(); // Let's move to value
	   
	   if (KEY_STATUS.equalsIgnoreCase(fieldName)) {
		   if (!STATUS_SUCCESS.equals(parser.getText())) {
			   break;
		   }
	   } else if (KEY_COUNT.equalsIgnoreCase(fieldName)) {
		   count = parser.getIntValue();
	   } else if (KEY_DATA.equalsIgnoreCase(fieldName)) {
		   records = new ArrayList<TradeRecord>(count);
		   TradeRecord re = null;
		   while (parser.nextToken() != JsonToken.END_ARRAY) {
			   if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
				   re = new TradeRecord();
				   parser.nextToken();
			   } else if (parser.getCurrentToken() == JsonToken.END_OBJECT) {
				   records.add(re);
				   continue;
			   }
			   
			   fieldName = parser.getCurrentName();
			   
			   parser.nextToken();
			   fieldValue = parser.getText();
			   
			   if (fieldName.equalsIgnoreCase("type")) {
				   if (TYPE_BUY.equalsIgnoreCase(fieldValue)) {
					   re.setTradeType(TradeRecord.TRADE_TYPE_BUY);
				   } else if (TYPE_SELL.equalsIgnoreCase(fieldValue)) {
					   re.setTradeType(TradeRecord.TRADE_TYPE_SELL);
				   }
			   } else if (fieldName.equalsIgnoreCase("price")) {
				   re.setPrice(Double.valueOf(fieldValue));
			   } else if (fieldName.equalsIgnoreCase("amount")) {
				   re.setTotalUnits(Double.valueOf(fieldValue));
			   } else if (fieldName.equalsIgnoreCase("total")) {
				   re.setTotalCost(Double.valueOf(fieldValue));
			   } else if (fieldName.equalsIgnoreCase("time")) {// NOTE: Time is specified as a unix timestamp with microseconds.
				   /* microsecond	μs	1 microsecond = 1,000 nanoseconds
					* millisecond	ms	1 millisecond = 1,000 microseconds */
				   re.setTradeTime((long) Arith.multiply(Double.valueOf(fieldValue), 1000));
			   }
		   }
	   }
	  }
	  parser.close(); // important to close both parser and underlying File reader
	  
	  return records;
	 }
	
	public static void main(String[] args) {
//		Mintpal.instance().getHistory("DOGE", Symbol.BTC.name());
		String watchedSymbol = "BC";
		String exchangeSymbol = Symbol.BTC.name();
//		List<TradeRecord> records = Mintpal.instance().getHistory(watchedSymbol, exchangeSymbol);
//		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ss");
//		for (TradeRecord r : rs) {
//			System.out.println(sd.format(new Date(r.getTradeTime())) + ", " + r.toReadableText());
//		}
//		System.out.println(SqlUtils.getInsertSql4TradeRecord(watchedSymbol, exchangeSymbol));
		
		Mintpal.instance().getMarketSummarys(exchangeSymbol);
		
	}

}

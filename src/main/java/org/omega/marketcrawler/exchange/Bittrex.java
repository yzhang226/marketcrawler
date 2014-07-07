package org.omega.marketcrawler.exchange;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Arith;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.net.NetUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Bittrex extends TradeOperator {
	
	private static final Log log = LogFactory.getLog(Bittrex.class);
	
	private static final Bittrex inst = new Bittrex();
	
	public static final String NAME = "bittrex";
	private static final String VERSION = "v1";
	
	// count	optional	a number between 1-100 for the number of entries to return (default = 20)
	private static final int DEFAULT_LIMITATION = 20;
	
	
	public static final String STATUS_TRUE = "true";
	
	public static final String KEY_SUCCESS = "success";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_RESULT = "result";
	
	private Bittrex() {}
	
	public static Bittrex instance() {
		return inst;
	}
	
	public String getBaseAPI() {
		return new StringBuilder("https://bittrex.com/api/").append(VERSION).append("/").toString();
	}
	
	// https://bittrex.com/api/v1/public/getmarkethistory?market=BTC-DOGE&count=5
	public String getMarketTradeAPI(String watchedSymbol, String exchangeSymbol) {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("public/getmarkethistory?market=").append(exchangeSymbol).append("-").append(watchedSymbol);
		
		return api.toString();
	}
	
	// https://bittrex.com/api/v1/public/getmarketsummaries
	public String getMarketSummaryAPI() {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("public/getmarketsummaries");
		return api.toString();
	}
	
	public String getHistoryJsonText(String watchedSymbol, String exchangeSymbol) {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("public/getmarkethistory?market=").append(exchangeSymbol).append("-").append(watchedSymbol);
		
		return NetUtils.accessDirectly(api.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<MarketSummary> getMarketSummaries() {
		List<MarketSummary> records = null;
		try {
			String recordText = NetUtils.accessDirectly(getMarketSummaryAPI());
			ObjectMapper mapper = new ObjectMapper();
			
			LinkedHashMap<String, Object> map = mapper.readValue(recordText, LinkedHashMap.class);
			
			if (STATUS_TRUE.equals(String.valueOf(map.get(KEY_SUCCESS)))) {
				records = new ArrayList<>(45);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				List<Map<String, Object>> data = (List<Map<String, Object>>) map.get(KEY_RESULT);
				MarketSummary summ = null;
				for (Map<String, Object> da : data) {
					summ = transfer(da, sdf);
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
	
	private MarketSummary transfer(Map<String, Object> da, SimpleDateFormat sdf) {
		MarketSummary summ = null;
		try {
			summ = new MarketSummary();
			summ.setOperator(NAME);
			String[] ss = da.get("MarketName").toString().split("-");
			summ.setWatchedSymbol(ss[1]);
			summ.setExchangeSymbol(ss[0]);
			
			if (da.get("Last") != null) summ.setLastPrice((double) da.get("Last"));
			if (da.get("PrevDay") != null) summ.setYesterdayPrice((double) da.get("PrevDay"));
	//		summ.setChange(da.get("change"));
			if (da.get("High") != null) summ.setHighest24h((double) da.get("High"));
			if (da.get("Low") != null) summ.setLowest24h((double) da.get("Low"));
			if (da.get("BaseVolume") != null) summ.setVolume24h((double) da.get("BaseVolume"));
			if (da.get("Volume") != null) summ.setCoinVolume24h((double) da.get("Volume"));
			if (da.get("Bid") != null) summ.setTopBid((double) da.get("Bid"));
			if (da.get("Ask") != null) summ.setTopAsk((double) da.get("Ask"));
			// "TimeStamp" : "2014-04-19T20:49:50.053" PSEUDOCOIN 2014-07-07T05:16:07
			String time = (String) da.get("TimeStamp");
			if (time.length() < 20 ) {
				time = time + ".000";
			}
			if (da.get("TimeStamp") != null) summ.setUpdateTime(new Timestamp(sdf.parse((String) da.get("TimeStamp")).getTime()));
		} catch (Exception e) {
			summ = null;
			log.error("", e);
		}
		return summ;
	}
	
	/**
	 *
	 * @see org.omega.marketcrawler.exchange.TradeOperator#getMarketTrades(java.lang.String, java.lang.String)
	 */
	public List<MarketTrade> getMarketTrades(String watchedSymbol, String exchangeSymbol) {
		List<MarketTrade> records = null;
		try {
			String recordText = getHistoryJsonText(watchedSymbol, exchangeSymbol);
			JsonFactory jfactory = new JsonFactory();
			JsonParser parser = jfactory.createParser(recordText);
			records = readData(parser);
		} catch (Exception e) {
			log.error("try to get and convert json history to object error.", e);
		}
		
		return records;
	}
	
	private List<MarketTrade> readData(JsonParser parser) throws Exception {
		  if (parser.nextToken() != JsonToken.START_OBJECT) {
		    throw new Exception("Expected data to start with an Object");
		  }

		  List<MarketTrade> records = null;
		  String fieldValue = null;
		  
		  while (parser.nextToken() != JsonToken.END_OBJECT) {
		   String fieldName = parser.getCurrentName();
		   parser.nextToken(); // Let's move to value
		   
		   if (KEY_SUCCESS.equalsIgnoreCase(fieldName)) {
			   if (!STATUS_TRUE.equals(parser.getText())) {
				   break;
			   }
		   } else if (KEY_MESSAGE.equalsIgnoreCase(fieldName)) {
			   // count = parser.getIntValue();
		   } else if (KEY_RESULT.equalsIgnoreCase(fieldName)) {
			   records = new ArrayList<MarketTrade>(DEFAULT_LIMITATION);
			   MarketTrade re = null;
			   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			   while (parser.nextToken() != JsonToken.END_ARRAY) {
				   if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
					   re = new MarketTrade();
					   re.setTradeType(MarketTrade.TRADE_TYPE_NA);
					   parser.nextToken();
				   } else if (parser.getCurrentToken() == JsonToken.END_OBJECT) {
					   records.add(re);
					   continue;
				   }
				   
				   fieldName = parser.getCurrentName();
				   
				   parser.nextToken();
				   fieldValue = parser.getText();
				   
				   if (fieldName.equalsIgnoreCase("Price")) {
					   re.setPrice(Double.valueOf(fieldValue));
				   } else if (fieldName.equalsIgnoreCase("Quantity")) {
					   re.setTotalUnits(Double.valueOf(fieldValue));
				   } else if (fieldName.equalsIgnoreCase("Total")) {
					   re.setTotalCost(Double.valueOf(fieldValue));
				   } else if (fieldName.equalsIgnoreCase("TimeStamp")) {// 2014-02-25T07:40:08.68
					   long millsec = 0;
					   try {
						   millsec = sdf.parse(fieldValue).getTime();
					   } catch (Exception e) {
						   log.error("parse date text[" + fieldValue + "] error.", e);
					   }
					   re.setTradeTime(millsec);
				   }
			   }
		   }
		  }
		  parser.close();
		  
		  return records;
	}
	
	public static void main(String[] args) {
//		Mintpal.instance().getHistory("DOGE", Symbol.BTC.name());
		String watchedSymbol = "BC";
		String exchangeSymbol = Symbol.BTC.name();
//		List<TradeRecord> records = Bittrex.instance().getHistory(watchedSymbol, exchangeSymbol);
//		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		for (TradeRecord r : records) {
//			System.out.println(sd.format(new Date(r.getTradeTime())) + ", " + r.toReadableText());
//		}
		
		List<MarketSummary> summs = Bittrex.instance().getMarketSummaries();
		for (MarketSummary summ : summs) {
			System.out.println(summ.toReadableText());
		}
	
	}

}

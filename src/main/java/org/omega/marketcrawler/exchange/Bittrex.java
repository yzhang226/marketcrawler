package org.omega.marketcrawler.exchange;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Arith;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.entity.TradeRecord;
import org.omega.marketcrawler.net.NetUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

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
	public String getHistoryJsonText(String watchedSymbol, String exchangeSymbol) {
		StringBuilder api = new StringBuilder(getBaseAPI());
		api.append("public/getmarkethistory?market=").append(exchangeSymbol).append("-").append(watchedSymbol);
		
		return NetUtils.accessDirectly(api.toString());
	}
	
	/**
	 *
	 * @see org.omega.marketcrawler.exchange.TradeOperator#getHistory(java.lang.String, java.lang.String)
	 */
	public List<TradeRecord> getHistory(String watchedSymbol, String exchangeSymbol) {
		List<TradeRecord> records = null;
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
	
	private List<TradeRecord> readData(JsonParser parser) throws Exception {
		  if (parser.nextToken() != JsonToken.START_OBJECT) {
		    throw new Exception("Expected data to start with an Object");
		  }

		  List<TradeRecord> records = null;
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
			   records = new ArrayList<TradeRecord>(DEFAULT_LIMITATION);
			   TradeRecord re = null;
			   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			   while (parser.nextToken() != JsonToken.END_ARRAY) {
				   if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
					   re = new TradeRecord();
					   re.setTradeType(TradeRecord.TRADE_TYPE_NA);
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
		List<TradeRecord> records = Bittrex.instance().getHistory(watchedSymbol, exchangeSymbol);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		for (TradeRecord r : records) {
			System.out.println(sd.format(new Date(r.getTradeTime())) + ", " + r.toReadableText());
		}
	
	}

}
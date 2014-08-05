package org.omega.marketcrawler.operator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public class Poloniex extends Operator {
	
	private static final Log log = LogFactory.getLog(Poloniex.class);
	
	public static final String NAME = "poloniex";
	
	public static final int DEFAULT_LIMIT = 100;
	public static final String TIME_PATTERN_BITTREX = "yyyy-MM-dd HH:mm:ss";
	
	private static final Poloniex inst = new Poloniex();
	private Poloniex() {}
	
	public static Poloniex instance() {
		return inst;
	}
	
	public String getTimePattern() {
		return TIME_PATTERN_BITTREX;
	}
	
	public DateTimeZone getTimeZone() {
		return Constants.ZONE_UTC;
	}
	
	public String getName() {
		return NAME;
	}
	
	private String getBasePublicAPI() {
		return "https://poloniex.com/public?";
	}

	// https://poloniex.com/public?command=returnTicker
	public String getMarketSummaryAPI() {
		StringBuilder api = new StringBuilder(getBasePublicAPI());
		api.append("command=returnTicker");
		return api.toString();
	}
	
	// https://poloniex.com/public?command=returnTradeHistory&currencyPair=BTC_NXT
	public String getMarketTradeAPI(WatchListItem item) {
		StringBuilder api = new StringBuilder(getBasePublicAPI());
		api.append("command=returnTradeHistory&currencyPair=").append(item.getExchangeSymbol()).append("_").append(item.getWatchedSymbol());
		return api.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<MarketSummary> transferJsonToMarketSummary(Object jsonObj) {
		LinkedHashMap<String, Object> json = (LinkedHashMap<String, Object>) jsonObj;
		List<MarketSummary> records = new ArrayList<>(50);
		MarketSummary summ = null;
		Map<String, String> da = null;
		String key = null;
		Timestamp curr = new Timestamp(System.currentTimeMillis());
		for (Entry<String, Object> ent : json.entrySet()) {
			key = ent.getKey();
			da = (Map<String, String>) ent.getValue();
			summ = new MarketSummary();
			try {
				summ.setOperator(NAME);
				String[] ss = key.split("_");
				summ.setExchangeSymbol(ss[0]);
				summ.setWatchedSymbol(ss[1]);
				
				summ.setLastPrice(Double.valueOf(da.get("last")));
				summ.setFluctuation(Double.valueOf(da.get("percentChange")));
				summ.setVolume24h(Double.valueOf(da.get("baseVolume")));
				summ.setCoinVolume24h(Double.valueOf(da.get("quoteVolume")));
				summ.setTopBid(Double.valueOf(da.get("highestBid")));
				summ.setTopAsk(Double.valueOf(da.get("lowestAsk")));
				summ.setUpdateTime(curr);
			} catch (Exception e) {
				summ = null;
				log.error("convert one json row error.", e);
			}
			if (summ != null) { records.add(summ); }
		}
		
		return records;
	}
	
	@SuppressWarnings("unchecked")
	public List<MarketTrade> transferJsonToMarketTrade(Object jsonObj) {
		List<Map<String, String>> json = (List<Map<String, String>>) jsonObj;
		List<MarketTrade> records = null;
		String fieldValue = null;
		MarketTrade re = null;
		records = new ArrayList<>(json.size());
		DateTimeFormatter formatter = getTimeFormatter();
		for (Map<String, String> da : json) {
			re = new MarketTrade();
			try {
				/* {"tradeID":"53010","date":"2014-07-26 06:35:07","type":"sell","rate":"0.00019453","amount":"69.50919137","total":"0.01352162"}
				 * {"tradeID":"53009","date":"2014-07-26 06:35:07","type":"sell","rate":"0.00019453","amount":"64.2514308","total":"0.01249883"} */
				if ((fieldValue = da.get("tradeID")) != null) { re.setTradeId(Integer.valueOf(fieldValue)); }
				if ((fieldValue = da.get("type")) != null) { re.setTradeType(MarketTrade.parseTradeType(fieldValue)); }
				if ((fieldValue = da.get("rate")) != null) { re.setPrice(Double.valueOf(fieldValue)); } 
				if ((fieldValue = da.get("amount")) != null) { re.setTotalUnits(Double.valueOf(fieldValue)); }
				if ((fieldValue = da.get("total")) != null) { re.setTotalCost(Double.valueOf(fieldValue)); }
				if ((fieldValue = da.get("date")) != null) { re.setTradeTime(parseMillsecs(fieldValue, formatter)); }
				
				records.add(re);
			} catch (Exception e) {
				log.error("convert one json row error.", e);
			}
		}
		
		return records;
	}
	
	public String reverseToJson(MarketTrade mt) {
		DateTimeFormatter formatter = getTimeFormatter();
		StringBuilder sb = new StringBuilder("{");
		sb
		  .append("\"").append("tradeID").append("\"").append(":").append("\"").append(mt.getTradeId()).append("\"").append(",")
		  .append("\"").append("date").append("\"").append(":").append("\"").append(formatter.print(mt.getTradeTime())).append("\"").append(",")
		  .append("\"").append("type").append("\"").append(":").append("\"").append(MarketTrade.formatTradeType(mt.getTradeType()).toLowerCase()).append("\"").append(",")
		  .append("\"").append("rate").append("\"").append(":").append("\"").append(Utils.justFormat(mt.getPrice())).append("\"").append(",")
		  .append("\"").append("amount").append("\"").append(":").append("\"").append(Utils.justFormat(mt.getTotalUnits())).append("\"").append(",")
		  .append("\"").append("total").append("\"").append(":").append("\"").append(Utils.justFormat(mt.getTotalCost())).append("\"")
		  ;
		sb.append("}");
		
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
//		List<MarketSummary> summs = Poloniex.inst.getMarketSummaries();
//		for (MarketSummary summ : summs) {
//			System.out.println(summ.toReadableText());
//		}
		WatchListItem item = new WatchListItem(NAME, "BC", Symbol.BTC.name());
		List<MarketTrade> trades = inst.getMarketTrades(item);
		for (MarketTrade mt : trades) {
			System.out.println(mt.toReadableText());
		}
	}

}

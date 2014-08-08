package org.omega.marketcrawler.operator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.net.MultiThreadedNetter;

import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class Operator {
	
//	private static final Log log = LogFactory.getLog(TradeOperator.class);
	
//	private int id;
	public abstract DateTimeZone getTimeZone();
	public abstract String getTimePattern();
	
//	DateTimeFormatter timeFormatter = null;
	protected DateTimeFormatter getTimeFormatter() {
//		if (timeFormatter == null) timeFormatter = DateTimeFormat.forPattern(getTimePattern()).withZone(getTimeZone());
		return DateTimeFormat.forPattern(getTimePattern()).withZone(getTimeZone());
	}
	
	protected long parseMillsecs(String time, DateTimeFormatter timeFormatter) throws Exception {
		long millsec = 0;
		try {
			if (Utils.isNotEmpty(time)) millsec = timeFormatter.parseMillis(time);
		} catch (Exception e) {
			throw new Exception("parse date text[" + time + "] error.", e);
		}

		return millsec;
	}
	
	public abstract String getName();
	
	public abstract String getMarketSummaryAPI();
	public abstract String getMarketTradeAPI(WatchListItem item);

	public abstract List<MarketSummary> transferJsonToMarketSummary(Object json);
	public abstract List<MarketTrade> transferJsonToMarketTrade(Object json);
	
	public abstract String reverseToJson(MarketTrade mt);
	
	

	public List<MarketSummary> getMarketSummaries() throws Exception {
		List<MarketSummary> records = null;
		try {
//			String recordText = NetUtils.get(getMarketSummaryAPI());
			String recordText = MultiThreadedNetter.inst().get(getMarketSummaryAPI());
			if (Utils.isEmptyOrNull(recordText) || recordText.length() < 20) return records;
			
			Object json = mapValue(recordText);
			
			records = transferJsonToMarketSummary(json);
		} catch (Throwable e) {
//			log.error("try to get and convert json Market Summary to object error.", e);
			throw new Exception("try to get and convert json Market Summary[" + getName() + "] to object error.", e);
		}
		
		return records;
	}
	
	/**
	 * 
	 * @param watchedSymbol - for example: MINT, DOGE
	 * @param exchangeSymbol - for example: BTC
	 * @return
	 */
	public List<MarketTrade> getMarketTrades(WatchListItem item) throws Exception {
		List<MarketTrade> records = null;
		try {
//			String recordText = NetUtils.get(getMarketTradeAPI(item));
			String recordText = MultiThreadedNetter.inst().get(getMarketTradeAPI(item));
			if (Utils.isEmptyOrNull(recordText) || recordText.length() < 20) return records;
			
			Object json = mapValue(recordText);
			
			records = transferJsonToMarketTrade(json);
		} catch (Throwable e) {
//			log.error("try to get and convert json Market Trade to object error.", e);
			throw new Exception("try to get and convert json Market Trade[" + item.toReadableText() + "] to object error.", e);
		}
		
		return records;
	}
	
	protected Object mapValue(String recordText) throws Throwable {
		Object json = null;
		ObjectMapper mapper = new ObjectMapper();
		
//		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		
		if (recordText.startsWith("{")) {
			json = mapper.readValue(recordText, LinkedHashMap.class);
		} else if (recordText.startsWith("[")) {
			json = mapper.readValue(recordText, ArrayList.class);
		} else {
			json = mapper.readValue(recordText, Object.class);
		}
		return json;
	}
	
}

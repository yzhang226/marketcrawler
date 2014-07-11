package org.omega.marketcrawler.operator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.net.NetUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class TradeOperator {
	
	private static final Log log = LogFactory.getLog(TradeOperator.class);
	
//	private int id;
	public abstract String getName();
	
	public abstract String getMarketSummaryAPI();
	public abstract String getMarketTradeAPI(WatchListItem item);

	public abstract List<MarketSummary> transferJsonToMarketSummary(Object json);
	public abstract List<MarketTrade> transferJsonToMarketTrade(Object json);
	

	public List<MarketSummary> getMarketSummaries() {
		List<MarketSummary> records = null;
		try {
			String recordText = NetUtils.get(getMarketSummaryAPI());
			
			Object json = mapValue(recordText);
			
			records = transferJsonToMarketSummary(json);
		} catch (Exception e) {
			log.error("try to get and convert json Market Summary to object error.", e);
		}
		
		return records;
	}
	
	/**
	 * 
	 * @param watchedSymbol - for example: MINT, DOGE
	 * @param exchangeSymbol - for example: BTC
	 * @return
	 */
	public List<MarketTrade> getMarketTrades(WatchListItem item) {
		List<MarketTrade> records = null;
		try {
			String recordText = NetUtils.get(getMarketTradeAPI(item));
			
			Object json = mapValue(recordText);
			
			records = transferJsonToMarketTrade(json);
		} catch (Exception e) {
			log.error("try to get and convert json Market Trade to object error.", e);
		}
		
		return records;
	}
	
	protected Object mapValue(String recordText) throws Exception {
		Object json = null;
		ObjectMapper mapper = new ObjectMapper();
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

package org.omega.marketcrawler.exchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.net.NetUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class TradeOperator {
	
	private static final Log log = LogFactory.getLog(TradeOperator.class);
	
//	private int id;
	public abstract String getName();
	
	public abstract String getMarketSummaryAPI();
	public abstract String getMarketTradeAPI(String watchedSymbol, String exchangeSymbol);

	public abstract List<MarketTrade> transferJsonToMarketTrade(Object json);
	public abstract List<MarketSummary> transferJsonToMarketSummary(Object json);
	
	/**
	 * 
	 * @param watchedSymbol - for example: MINT, DOGE
	 * @param exchangeSymbol - for example: BTC
	 * @return
	 */
	public List<MarketTrade> getMarketTrades(String watchedSymbol, String exchangeSymbol) {
		List<MarketTrade> records = null;
		try {
			String recordText = NetUtils.accessDirectly(getMarketTradeAPI(watchedSymbol, exchangeSymbol));
			
			Object json = mapValue(recordText);
			
			records = transferJsonToMarketTrade(json);
		} catch (Exception e) {
			log.error("try to get and convert json Market Trade to object error.", e);
		}
		
		return records;
	}
	
	private Object mapValue(String recordText) throws Exception {
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
	
	public List<MarketSummary> getMarketSummaries() {
		List<MarketSummary> records = null;
		try {
			String recordText = NetUtils.accessDirectly(getMarketSummaryAPI());
			
			Object json = mapValue(recordText);
			
			records = transferJsonToMarketSummary(json);
		} catch (Exception e) {
			log.error("try to get and convert json Market Summary to object error.", e);
		}
		
		return records;
	}
	
}

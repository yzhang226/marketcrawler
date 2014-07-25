package org.omega.marketcrawler.operator;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public class Poloniex extends Operator {
	
	private static final Log log = LogFactory.getLog(Poloniex.class);
	
	public static final String NAME = "poloniex";
	
	public static final String TIME_PATTERN_BITTREX = "yyyy-MM-dd HH:mm:ss";
	
	private static final Poloniex inst = new Poloniex();
	private Poloniex() {}
	
	public static Poloniex instance() {
		return inst;
	}
	
	@Override
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
		/*
		 * [{"date":"2014-02-10 04:23:23","type":"buy","rate":"0.00007600","amount":"140","total":"0.01064"},
		 * {"date":"2014-02-10 01:19:37","type":"buy","rate":"0.00007600","amount":"655","total":"0.04978"}, ... ]
		 */
		String field = null;
		MarketTrade re = null;
		records = new ArrayList<>(json.size());
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN_BITTREX);
		for (Map<String, String> da : json) {
			re = new MarketTrade();
			try {
				if ((field = da.get("type")) != null) {
					re.setTradeType(MarketTrade.parseTradeType(field));
				}
				if ((field = da.get("rate")) != null) { re.setPrice(Double.valueOf(field)); } 
				if ((field = da.get("amount")) != null) { re.setTotalUnits(Double.valueOf(field)); }
				if ((field = da.get("total")) != null) { re.setTotalCost(Double.valueOf(field)); }
				if ((field = da.get("date")) != null) {// 2014-02-10 04:23:23
					re.setTradeTime(parseMillsecs(field, sdf));
				}
			} catch (Exception e) {
				re = null;
				log.error("convert one json row error.", e);
			}
			if (re != null) { records.add(re); }
		}
		
		return records;
	}
	
	private long parseMillsecs(String time, SimpleDateFormat sdf) {
		long millsec = 0;
		try {
			if (Utils.isNotEmpty(time)) millsec = sdf.parse(time).getTime();
		} catch (Exception e) {
			log.error("parse date text[" + time + "] error.", e);
		}

		return millsec;
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

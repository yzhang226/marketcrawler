package org.omega.marketcrawler.operator;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public class Cryptsy extends TradeOperator {

	private static final Log log = LogFactory.getLog(Cryptsy.class);
	
	private static final Cryptsy inst = new Cryptsy();
	
	public static final String NAME = "cryptsy";
	
	public static final String TIME_PATTERN_CRYPTSY = "yyyy-MM-dd HH:mm:ss";
	
	public static final Integer SUCCESS_1 = 1;
	public static final Integer SUCCESS_0 = 0;
	
	/** 1 - sucessful    - return
	 *  0 - unsuccessful - error */
	public static final String KEY_SUCCESS = "success";
	public static final String KEY_RETURN = "return";
	public static final String KEY_ERROR = "error";
	public static final String KEY_MARKETS = "markets";
	public static final String KEY_RECENTTRADES = "recenttrades";
	
	private Cryptsy() {}
	
	public static Cryptsy instance() {
		return inst;
	}
	
	// https://api.cryptsy.com/api
	public String getBasePublicAPI() {
		return "http://pubapi.cryptsy.com/api.php?";
	}
	
	public String getName() {
		return NAME;
	}

	// http://pubapi.cryptsy.com/api.php?method=marketdatav2 
	// data is very large - about 8MB
	public String getMarketSummaryAPI() {
		return new StringBuilder(getBasePublicAPI()).append("method=marketdatav2").toString();
	}

	// http://pubapi.cryptsy.com/api.php?method=singlemarketdata&marketid={MARKET ID} 
	// data is not small - about 37KB
	public String getMarketTradeAPI(WatchListItem item) {
		return new StringBuilder(getBasePublicAPI()).append("method=singlemarketdata&marketid=").append(item.getMarketId()).toString();
	}

	@SuppressWarnings("unchecked")
	public List<MarketSummary> transferJsonToMarketSummary(Object json) {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) json;
		List<MarketSummary> records = new ArrayList<>(50);
		
		Integer success = (Integer) map.get(KEY_SUCCESS);
		if (SUCCESS_1.equals(success)) {
			map = (LinkedHashMap<String, Object>) map.get(KEY_RETURN);
			map = (LinkedHashMap<String, Object>) map.get(KEY_MARKETS);
			SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN_CRYPTSY);
			Map<String, Object> da = null;
			MarketSummary summ = null;
			for (String key : map.keySet()) {
				da = (Map<String, Object>) map.get(key);
				summ = new MarketSummary();
				try {
					summ.setOperator(NAME);
					summ.setWatchedSymbol((String) da.get("primarycode"));
					summ.setExchangeSymbol((String) da.get("secondarycode"));
					summ.setMarketId(Integer.valueOf((String) da.get("marketid")));
					summ.setWatchedCoinName((String) da.get("primaryname"));
					summ.setExchangeCoinName((String) da.get("secondaryname"));
					
					summ.setLastPrice(Double.valueOf((String) da.get("lasttradeprice")));
					summ.setVolume24h(Double.valueOf((String) da.get("volume")));
					
					summ.setUpdateTime(new Timestamp(parseMillsecs((String) da.get("lasttradetime"), sdf)));
				} catch (Exception e) {
					summ = null;
					log.error("", e);
				}
				if (summ != null) { records.add(summ); }
			}
		} else {
			log.error("Transfer Json To Market Summary error: " + map.get(KEY_ERROR));
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
	
	@SuppressWarnings("unchecked")
	public List<MarketTrade> transferJsonToMarketTrade(Object json) {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) json;
		List<MarketTrade> records = new ArrayList<>(50);
		
		Integer success = (Integer) map.get(KEY_SUCCESS);
		if (SUCCESS_1.equals(success)) {
			map = (LinkedHashMap<String, Object>) map.get(KEY_RETURN);
			map = (LinkedHashMap<String, Object>) map.get(KEY_MARKETS);
			map = (LinkedHashMap<String, Object>) map.values().iterator().next();
			List<Map<String, String>> trades = (List<Map<String, String>>) map.get(KEY_RECENTTRADES);
			SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN_CRYPTSY);
			MarketTrade re = null;
			String fieldValue = null;
			for (Map<String, String> da : trades) {
				re = new MarketTrade();
				try {
					if ((fieldValue = da.get("type")) != null) {
						re.setTradeType(MarketTrade.parseTradeType(fieldValue));
					}
					if ((fieldValue = da.get("price")) != null) { re.setPrice(Double.valueOf(fieldValue)); } 
					if ((fieldValue = da.get("quantity")) != null) { re.setTotalUnits(Double.valueOf(fieldValue)); }
					if ((fieldValue = da.get("total")) != null) { re.setTotalCost(Double.valueOf(fieldValue)); }
					if ((fieldValue = da.get("time")) != null) {// 2014-07-11 04:33:30
						re.setTradeTime(parseMillsecs(fieldValue, sdf));
					}
				} catch (Exception e) {
					re = null;
					log.error("", e);
				}
				if (re != null) { records.add(re); }
			}
		} else {
			log.error("Transfer Json To Market Trade error: " + map.get(KEY_ERROR));
		}
		
		return records;
	}
	
	public static void main(String[] args) throws Exception {
//		List<MarketSummary> summs = Cryptsy.inst.getMarketSummaries();
//		for (MarketSummary summ : summs) {
//			System.out.println(summ.toReadableText());
//		}
//		new MarketSummaryService().save(summs);
		
		WatchListItem item = new WatchListItem();
		item.setMarketId(132);
		List<MarketTrade> trades = Cryptsy.inst.getMarketTrades(item);
		for (MarketTrade mt : trades) {
			System.out.println(mt.toReadableText());
		}
	}
	

}

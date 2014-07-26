package org.omega.marketcrawler.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public final class MyCache {

	public static final int QUERY_LIMIT = 200;
	
	private static final MyCache pool = new MyCache();
	
	private MyCache() {}
	
	public static MyCache inst() {
		return pool;
	}
	
	// 
	private Map<WatchListItem, MarketTrade> latestTradeMap = new HashMap<>();
//	private Map<WatchListItem, List<MarketTrade>> overlapTradeMap = new HashMap<>();
//	private Map<WatchListItem, Set<MarketTrade>> pooledTradeMap = new HashMap<>();
	private Set<WatchListItem> watchedItems = new HashSet<>();
	private Set<String> watchedSymbols = new HashSet<>();
	
	
	public MarketTrade getLatestTrade(WatchListItem item) {
		MarketTrade last = latestTradeMap.get(item);
		return last;
	}
	
	public void putLatestTrade(WatchListItem item, MarketTrade latest) {
		latestTradeMap.put(item, latest);
	}
	
	public Set<WatchListItem> getWatchedItems() {
		return watchedItems;
	}
	
	public void addAllItems(List<WatchListItem> items) {
		if (Utils.isEmpty(items)) return;
		for (WatchListItem item : items) {
			watchedItems.add(item);
			watchedSymbols.add(item.getWatchedSymbol());
		}
	}
	
	public boolean cointainsItem(WatchListItem item) {
		return watchedItems.contains(item);
	}
	
	public boolean containsSymbol(String symbol) {
		return watchedSymbols.contains(symbol);
	}
	
	public void clear() {
		watchedItems.clear();
		watchedSymbols.clear();
//		pooledTradeMap.clear();
//		overlapTradeMap.clear();
	}

//	public boolean addMarketTrade(WatchListItem item, MarketTrade mt) {
//		if (mt == null) return false;
//		return getCachedTrades(item).add(mt);
//	}
	
//	public boolean addMarketTrade(WatchListItem item, List<MarketTrade> trades) {
//		if (Utils.isEmpty(trades)) return false;
//		return getCachedTrades(item).addAll(trades);
//	}
	
//	public Set<MarketTrade> getCachedTrades(WatchListItem item) {
//		Set<MarketTrade> trades = pooledTradeMap.get(item);
//		if (Utils.isEmpty(trades)) {
//			trades = new HashSet<>(QUERY_LIMIT);
//			pooledTradeMap.put(item, trades);
//		}
//		return trades;
//	}
	
//	public MarketTrade getCachedTrade(WatchListItem item, long tradeTime, byte tradeType) {
//		Set<MarketTrade> trades = getCachedTrades(item);
//		for (MarketTrade mt : trades) {
//			if (mt.getTradeTime() == tradeTime && mt.getTradeType() == tradeType) {
//				return mt;
//			}
//		}
//		return null;
//	}
	
//	public boolean containsCachedTrade(WatchListItem item, long tradeTime, byte tradeType) {
//		Set<MarketTrade> trades = getCachedTrades(item);
//		for (MarketTrade mt : trades) {
//			if (mt.getTradeTime() == tradeTime && mt.getTradeType() == tradeType) {
//				return true;
//			}
//		}
//		return false;
//	}
	
//	public MarketTrade getOverlapedTrade(WatchListItem item, long tradeTime, byte tradeType) {
//		List<MarketTrade> overlaps = getOverlapTrades(item);
//		for (MarketTrade mt : overlaps) {
//			if (mt.getTradeTime() == tradeTime && mt.getTradeType() == tradeType) {
//				return mt;
//			}
//		}
//		return null;
//	}
	
//	public boolean containsOverlapedTrade(WatchListItem item, long tradeTime, byte tradeType) {
//		List<MarketTrade> overlaps = getOverlapTrades(item);
//		for (MarketTrade mt : overlaps) {
//			if (mt.getTradeTime() == tradeTime && mt.getTradeType() == tradeType) {
//				return true;
//			}
//		}
//		return false;
//	}
	
//	public boolean addOverlapTarde(WatchListItem item, MarketTrade lap) {
//		if (lap == null) return false;
//		return getOverlapTrades(item).add(lap);
//	}
	
//	public boolean addOverlapTarde(WatchListItem item, List<MarketTrade> laps) {
//		if (Utils.isEmpty(laps)) return false;
//		return getOverlapTrades(item).addAll(laps);
//	}
	
//	public Map<WatchListItem, List<MarketTrade>> getOverlapTradeMap() {
//		return overlapTradeMap;
//	}
	
//	public List<MarketTrade> getOverlapTrades(WatchListItem item) {
//		List<MarketTrade> trades = overlapTradeMap.get(item);
//		if (Utils.isEmpty(trades)) {
//			trades = new ArrayList<>(QUERY_LIMIT);
//			overlapTradeMap.put(item, trades);
//		}
//		return trades;
//	}
	
}

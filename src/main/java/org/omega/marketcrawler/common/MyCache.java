package org.omega.marketcrawler.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.service.MarketTradeService;
import org.omega.marketcrawler.service.WatchListItemService;

public final class MyCache {

	private static final Object lock = new Object();
	
	private static final MyCache cache = new MyCache();
	
	private MyCache() {}
	
	public static MyCache inst() {
		return cache;
	}
	
	private Map<WatchListItem, MarketTrade> latestTradeMap = new ConcurrentHashMap<>();
	private Set<WatchListItem> watchedItems = new HashSet<>();
	
	public MarketTrade getLatestTrade(WatchListItem item) {
		MarketTrade last = latestTradeMap.get(item);
		return last;
	}
	
	public void putLatestTrade(WatchListItem item, MarketTrade latest) {
		if (latest == null)  latest = MarketTrade.EMPTY_TRADE;
		latestTradeMap.put(item, latest);
	}
	
	public Set<WatchListItem> getWatchedItems() {
		synchronized (lock) {
			return watchedItems;
		}
	}
	
	private void addAllItems(List<WatchListItem> items) {
		if (Utils.isEmpty(items)) return;
		for (WatchListItem item : items) {
			watchedItems.add(item);
		}
	}
	
	private void clear() {
		watchedItems.clear();
	}
	
	public List<WatchListItem> refresh() throws Exception {
		synchronized (lock) {
			clear();
			
			List<WatchListItem> addedItems = new ArrayList<>();
			WatchListItemService wiser = new WatchListItemService();
			wiser.initWatchedItem();
			
			MarketTradeService mtser = new MarketTradeService();
			List<WatchListItem> items = wiser.findActiveItems();
			for (WatchListItem it : items) {
				if (mtser.initWatchedTable(it)) { addedItems.add(it); }
			}
			
			addAllItems(items);
			
			// refresh the latest market trade
			for (WatchListItem item : items) {
				putLatestTrade(item, mtser.findLatestTrade(item));
			}
			
			return addedItems;
		}
	}

}

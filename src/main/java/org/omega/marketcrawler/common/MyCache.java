package org.omega.marketcrawler.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omega.marketcrawler.entity.WatchListItem;

public final class MyCache {

	private static final MyCache pool = new MyCache();
	
	private MyCache() {}
	
	public static MyCache inst() {
		return pool;
	}
	
	// 
	private Map<WatchListItem, Set<Long>> pooledPKs = new HashMap<>();
	private Set<WatchListItem> watchedItems = new HashSet<>();
	private Set<String> watchedSymbols = new HashSet<>();
	
	public Set<WatchListItem> getWatchedItems() {
		return watchedItems;
	}
	
	public void clearAllItems() {
		watchedItems.clear();
		watchedSymbols.clear();
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
	
	public void clearAllPKs() {
		pooledPKs.clear();
	}
	
	public Set<Long> getCachedPKs(WatchListItem item) {
		Set<Long> keys = pooledPKs.get(item);
		if (Utils.isEmpty(keys)) {
			keys = new HashSet<>(200);
			pooledPKs.put(item, keys);
		}
		return keys;
	}
	
	public boolean containsKey(WatchListItem item, Long key) {
		return getCachedPKs(item).contains(key);
	}
	
	public boolean addPK(WatchListItem item, Long key) {
		return getCachedPKs(item).add(key);
	}
	
	
}

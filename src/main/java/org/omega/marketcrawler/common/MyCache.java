package org.omega.marketcrawler.common;

import java.util.HashMap;
import java.util.HashSet;
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
	
	public Set<WatchListItem> getWatchedItmes() {
		return watchedItems;
	}
	
	public Set<String> getWatchedSymbols() {
		return watchedSymbols;
	}
	
	public boolean cointainsItem(WatchListItem item) {
		return watchedItems.contains(item);
	}
	
	public boolean containsSymbol(String symbol) {
		return watchedSymbols.contains(symbol);
	}
	
	
	public Set<Long> getCachedPKs(WatchListItem item) {
		Set<Long> keys = pooledPKs.get(item);
		if (Utils.isEmpty(keys)) {
			keys = new HashSet<>(300);
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

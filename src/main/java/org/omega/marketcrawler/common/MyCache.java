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
	private Map<WatchListItem, Set<Long>> pooledKeys = new HashMap<>();
	private Set<WatchListItem> watchedItmes = new HashSet<>();
	
	public Set<WatchListItem> getWatchedItmes() {
		return watchedItmes;
	}
	
	public Set<Long> getCachedKeys(WatchListItem item) {
		Set<Long> keys = pooledKeys.get(item);
		if (Utils.isEmpty(keys)) {
			keys = new HashSet<>(300);
			pooledKeys.put(item, keys);
		}
		return keys;
	}
	
	public boolean containsKey(WatchListItem item, Long key) {
		return getCachedKeys(item).contains(key);
	}
	
	public boolean addKey(WatchListItem item, Long key) {
		return getCachedKeys(item).add(key);
	}
	
	
}

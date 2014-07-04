package org.omega.marketcrawler.common;

import java.util.HashSet;
import java.util.Set;

import org.omega.marketcrawler.entity.WatchListItem;

public final class ConstantPool {

	private static final ConstantPool pool = new ConstantPool();
	
	private ConstantPool() {}
	
	public static ConstantPool inst() {
		return pool;
	}
	
	// 
	private Set<WatchListItem> watchedItmes = new HashSet<>();
	
	public Set<WatchListItem> getWatchedItmes() {
		return watchedItmes;
	}
	
}

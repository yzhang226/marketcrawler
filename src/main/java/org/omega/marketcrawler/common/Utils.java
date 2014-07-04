package org.omega.marketcrawler.common;

import java.net.URL;
import java.util.Collection;

import org.omega.marketcrawler.entity.WatchListItem;

public final class Utils {


	public static boolean isEmpty(String text) {
		return text == null || text.trim().length() == 0;
	}
	
	public static boolean isNotEmpty(String text) {
		return !isEmpty(text);
	}
	
	public static boolean isNotEmpty(Object[] objs) {
		return objs != null && objs.length != 0;
	}
	
	public static boolean isEmpty(Collection<?> list) {
		return list == null || list.isEmpty();
	}
	
	public static boolean isNotEmpty(Collection<?> list) {
		return !isEmpty(list);
	}
	
	public static String getResourcePath(String resource) {
		URL url = Utils.class.getClassLoader().getResource(resource);
		return url == null ? "" : url.getPath();
	}
	
	public static String getMarketTradeTable(WatchListItem item) {
		return new StringBuilder("trade_")
		.append(item.getOperator().toLowerCase()).append("_")
		.append(item.getExchangeSymbol().toLowerCase()).append("_")
		.append(item.getWatchedSymbol().toLowerCase()).toString();
	}
	
}

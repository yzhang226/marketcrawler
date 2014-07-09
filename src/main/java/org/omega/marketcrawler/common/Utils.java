package org.omega.marketcrawler.common;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public final class Utils {

	public static final long SECONDS_ONE_HOUR = 60 * 60;
	public static final long SECONDS_ONE_DAY = 24 * 60 * 60;

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
	
	public static File tryGetResourceFile(String resource) {
		String resourcePath = getResourcePath(resource);
		File resourceFile = new File(resourcePath);
		if (Utils.isEmpty(resourcePath) || !resourceFile.exists()) {
			resourceFile = new File(resource);
		}
		
		return resourceFile;
	}
	
	public static Date addSeconds(Date d, long secs) {
		long nSecs = d.getTime() + (secs * 1000);
		return new Date(nSecs);
	}
	
	public static String getMarketTradeTable(WatchListItem item) {
		return new StringBuilder("trade_")
		.append(item.getOperator().toLowerCase()).append("_")
		.append(item.getExchangeSymbol().toLowerCase()).append("_")
		.append(item.getWatchedSymbol().toLowerCase()).toString();
	}
	
	public static int countBatchResult(int [] resu) {
		if (resu == null || resu.length == 0) return 0;
		int total = 0;
		for (int re : resu) {
			if (re > 0) total = total + re;
		}
		
		return total;
	}
	
	public static void removeRepeated(WatchListItem item, List<MarketTrade> records) {
		Iterator<MarketTrade> iter = records.iterator();
		MarketTrade nxt = null;
		while (iter.hasNext()) {
			nxt = iter.next();
			if (MyCache.inst().containsKey(item, nxt.getTradeTime())) {
				iter.remove();
			} else {
				// MyCache.inst().addKey(item, nxt.getTradeTime());
			}
		}
	}
	
	
	
	
}

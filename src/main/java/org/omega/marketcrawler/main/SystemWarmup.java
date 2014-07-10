package org.omega.marketcrawler.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.AltCoinService;
import org.omega.marketcrawler.db.MarketSummaryService;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.entity.WatchListItem;

public final class SystemWarmup {
	
	private static final Log log = LogFactory.getLog(SystemWarmup.class);
	
	private static final SystemWarmup sys = new SystemWarmup();
	
	private SystemWarmup() {}
	
	public static SystemWarmup inst() {
		return sys;
	}
	
	public void warmup() {
		// first, fetch all market summaries
		MarketSummaryService ser = new MarketSummaryService();
		try {
			ser.refreshAllSummaries();
		} catch (SQLException e) {
			log.error("init market summaries error.", e);
		}
		
		// load watch list item for watched coins
		AltCoinService altSer = new AltCoinService();
		try {
			List<String> watchedSymbols = altSer.findWatchedSymbols();
			System.out.println(watchedSymbols);
			List<WatchListItem> items = ser.findWatchedItmes(watchedSymbols);
			
			MarketTradeService mtser = new MarketTradeService();
			for (WatchListItem item : items) {
				if (!mtser.existWatchedTable(item)) {
					mtser.createWatchedTable(item);
					log.info("create trade table for item[" + item.toSimpleText() + "].");
				}
			}
			log.info(getWatchedItemsInfo(items));
			
			MyCache.inst().addAllItems(items);
			
			for (WatchListItem it : items) {
				List<Long> keys = mtser.findAllTradeTimes(it);
				if (Utils.isNotEmpty(keys)) {
					Set<Long> itemKeys = MyCache.inst().getCachedPKs(it);
					for (Long tt : keys) {
						itemKeys.add(tt);
					}
				}
			}
			
		} catch (Exception e) {
			log.error("init watch list item error.", e);
		}
		
	}
	
	private String getWatchedItemsInfo(List<WatchListItem> items) {
		StringBuilder sb = new StringBuilder("There are ");
		sb.append(items.size()).append(" items that being watched.").append("\n");
		for (WatchListItem item : items) {
			sb.append(item.toReadableText()).append("|");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		inst().warmup();
	}
	
}

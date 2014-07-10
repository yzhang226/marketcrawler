package org.omega.marketcrawler.job;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.AltCoinService;
import org.omega.marketcrawler.db.MarketSummaryService;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.entity.WatchListItem;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WatchNewCoinsJob implements Job {
	
	private static final Log log = LogFactory.getLog(WatchNewCoinsJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		AltCoinService altSer = new AltCoinService();
		try {
			List<String> addedSymbols = new ArrayList<>();
			List<String> watchedSymbols = altSer.findWatchedSymbols();
			for (String symbol : watchedSymbols) {
				if (!MyCache.inst().containsSymbol(symbol)) {
					addedSymbols.add(symbol);
				}
			}
			
			if (Utils.isNotEmpty(addedSymbols)) {
				refreshMarketSummaries();
				
				initWatchedItems(addedSymbols);
			}
			
		} catch (Exception e) {
			log.error("init watch list item error.", e);
		}
		
		log.info("end");
	}
	
	private void refreshMarketSummaries() {
		MarketSummaryService ser = new MarketSummaryService();
		try {
			ser.refreshAllSummaries();
		} catch (SQLException e) {
			log.error("init market summaries error.", e);
		}
	}
	
	private void initWatchedItems(List<String> addedSymbols) throws SQLException {
		MarketSummaryService ser = new MarketSummaryService();
		List<WatchListItem> addedItems = ser.findWatchedItmes(addedSymbols);
		System.out.println("New Watched Items " + addedItems);
		
		MarketTradeService mtser = new MarketTradeService();
		for (WatchListItem item : addedItems) {
			if (!mtser.existWatchedTable(item)) {
				log.info("create trade table for item[" + item.toSimpleText() + "].");
				mtser.createWatchedTable(item);
			}
		}
		log.info(getWatchedItemsInfo(addedItems));
		
		MyCache.inst().addAllItems(addedItems);
	}
	
	private String getWatchedItemsInfo(List<WatchListItem> items) {
		StringBuilder sb = new StringBuilder("There are ");
		sb.append(items.size()).append(" items that being watched.").append("\n");
		for (WatchListItem item : items) {
			sb.append(item.toReadableText()).append("|");
		}
		return sb.toString();
	}

}

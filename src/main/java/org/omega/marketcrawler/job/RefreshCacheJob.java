package org.omega.marketcrawler.job;

import static org.omega.marketcrawler.common.MyCache.QUERY_LIMIT;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.db.WatchListItemService;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RefreshCacheJob implements Job {
	
	private static final Log log = LogFactory.getLog(RefreshCacheJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		MyCache.inst().clear();
		
		try {
			// refresh watch list item
			WatchListItemService wiser = new WatchListItemService();
			wiser.initWatchedItem();
			
			MarketTradeService mtser = new MarketTradeService();
			List<WatchListItem> items = wiser.findActiveItems();
			for (WatchListItem it : items) {
				if (mtser.initWatchedTable(it)) { log.info("New Add Watched Item: " + it.toReadableText()); }
			}
			
			MyCache.inst().addAllItems(items);
			log.info(getWatchedItemsInfo(items));
			
			// refresh overlap market trades only once
			for (WatchListItem item : items) {
				try {
					List<MarketTrade> trades = mtser.findOverlapMarketTrade(item, QUERY_LIMIT);
					MyCache.inst().getOverlapTrades(item).addAll(trades);
				} catch (Exception e) {
					log.error("add overlap market_trade data to cache error.", e);
				}
			}
			
			// refresh cache market trades
			for (WatchListItem item : items) {
				try {
					List<MarketTrade> trades = mtser.findTopTrades(item, QUERY_LIMIT);
					MyCache.inst().addMarketTrade(item, trades);
				} catch (Exception e) {
					log.error("add top market_trade data to cache error.", e);
				}
			}
		} catch (Exception e) {
			String error = "Refresh Cache Job error.";
			log.error(error, e);
			throw new JobExecutionException(error, e);
		}
		
		log.info("end");
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

package org.omega.marketcrawler.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.service.MarketTradeService;
import org.omega.marketcrawler.service.WatchListItemService;
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
			
			// refresh the latest market trade
			for (WatchListItem item : items) {
				try {
					MyCache.inst().putLatestTrade(item, mtser.findLatestTrade(item));
				} catch (Exception e) {
					log.error("put latest market_trade to cache error.", e);
				}
			}
			
		} catch (Exception e) {
			String error = "Refresh Cache Job error.";
			log.error(error, e);
//			throw new JobExecutionException(error, e);
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

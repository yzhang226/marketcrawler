package org.omega.marketcrawler.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.db.WatchListItemService;
import org.omega.marketcrawler.entity.WatchListItem;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RefreshWatchedItemJob implements Job {
	
	private static final Log log = LogFactory.getLog(RefreshWatchedItemJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			WatchListItemService wiser = new WatchListItemService();
			wiser.initWatchedItem();
			
			MarketTradeService mtser = new MarketTradeService();
			List<WatchListItem> items = wiser.findActiveItems();
			for (WatchListItem it : items) {
				if (mtser.initWatchedTable(it)) {
					log.info("New Add Watched Item: " + it.toReadableText());
				}
			}
			
			MyCache.inst().clearAllItems();
			MyCache.inst().addAllItems(items);
			
			log.info(getWatchedItemsInfo(items));
		} catch (Exception e) {
			log.error("Refresh Watched Item error.", e);
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

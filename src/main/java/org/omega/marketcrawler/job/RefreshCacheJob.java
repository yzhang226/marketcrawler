package org.omega.marketcrawler.job;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.net.MultiThreadedNetter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RefreshCacheJob implements Job {
	
	private static final Log log = LogFactory.getLog(RefreshCacheJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			MultiThreadedNetter.inst().refresh();
			log.info("Refresh Multi Netter success.");
		} catch (Exception e) {
			log.error("Refresh Multi Netter error.", e);
		}
		
		try {
			List<WatchListItem> addedItems = MyCache.inst().refresh();
			log.info("Refresh MyCache ok.");
			
			if (Utils.isNotEmpty(addedItems)) { 
				StringBuilder sb = new StringBuilder();
				for (WatchListItem item : addedItems) {
					sb.append(item.toReadableText()).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				log.info("New Add WatchListItem is " + sb.toString());
			}
			log.info(getWatchedItemsInfo(MyCache.inst().getWatchedItems()));
		} catch (Exception e) {
			log.error("Refresh MyCache error.", e);
		}
		
		log.info("end");
		
		
	}
	
	private String getWatchedItemsInfo(Collection<WatchListItem> items) {
		StringBuilder sb = new StringBuilder("There are ");
		sb.append(items.size()).append(" items that being watched.").append("\n");
		for (WatchListItem item : items) {
			sb.append(item.toReadableText()).append("|");
		}
		return sb.toString();
	}

}

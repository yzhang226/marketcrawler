package org.omega.marketcrawler.job;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.thread.MarketTradeThread;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MarketTradeCrawlerJob implements Job {
	
	private static final Log log = LogFactory.getLog(MarketTradeCrawlerJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start execute job");
		
		try {
			Set<WatchListItem> items = MyCache.inst().getWatchedItems();
			for (WatchListItem item : items) {
				new MarketTradeThread(item).start();
			}
		} catch (Exception e) {
			String error = "Execution Market Trade CrawlerJob error.";
			log.error(error, e);
//			throw new JobExecutionException(error, e);
		}
		
	}

}

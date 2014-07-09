package org.omega.marketcrawler.job;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.thread.MarketTradeSpider;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MarketTradeCrawlerJob implements Job {
	
	private static final Log log = LogFactory.getLog(MarketTradeCrawlerJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start execute job");
		
		Set<WatchListItem> items = MyCache.inst().getWatchedItmes();
		for (WatchListItem item : items) {
			new MarketTradeSpider(item).start();
		}
	}

}

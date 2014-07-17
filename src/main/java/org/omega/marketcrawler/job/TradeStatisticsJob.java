package org.omega.marketcrawler.job;

import java.util.Set;

import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.thread.TradeStatisticsThread;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TradeStatisticsJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		Set<WatchListItem> items = MyCache.inst().getWatchedItems();
		for (WatchListItem item : items) {
			new TradeStatisticsThread(item).start();
		}
	}
	
	

}

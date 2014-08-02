package org.omega.marketcrawler.job;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
			ExecutorService exec = Executors.newFixedThreadPool(6);
//			CompletionService<AltCoin> pool = new ExecutorCompletionService<>(exec);
			
			Set<WatchListItem> items = MyCache.inst().getWatchedItems();
			for (WatchListItem item : items) {
				exec.submit(new MarketTradeThread(item));
			}
			exec.shutdown();
			while (!exec.isTerminated()) {
				exec.awaitTermination(2, TimeUnit.SECONDS);
			}
			
		} catch (Exception e) {
			String error = "Execution Market Trade CrawlerJob error.";
			log.error(error, e);
		}
		log.info("end");
	}

}

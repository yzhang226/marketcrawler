package org.omega.marketcrawler.job;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.thread.MarketTradeThread;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.omega.marketcrawler.common.Constants.*;

public class MarketTradeCrawlerJob implements Job {
	
	private static final Log log = LogFactory.getLog(MarketTradeCrawlerJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		long start = System.currentTimeMillis();
		log.info("start execute job");
		
		try {
			ExecutorService exec = Executors.newFixedThreadPool(6);
			
			Set<WatchListItem> items = MyCache.inst().getWatchedItems();
			for (WatchListItem item : items) {
				exec.submit(new MarketTradeThread(item));
			}
			exec.shutdown();
			
			int usedMillis = 0;
			while (!exec.isTerminated() ) {
				exec.awaitTermination(2, TimeUnit.SECONDS);
				usedMillis = Utils.changeMillsToSeconds(System.currentTimeMillis() - start);
				if (usedMillis > MAX_AWAIT_MILLIS) {
					log.info("Used Millis greater than Max_Await_Millis.");
					List<Runnable> runners = exec.shutdownNow();
					MarketTradeThread waiter = null;
					for (Runnable runner : runners) {
						waiter = (MarketTradeThread) runner;
						try {
							if (waiter.isAlive()) waiter.interrupt();
						} catch (Exception e) {
							log.error("Try to interrupt MarketTradeThread["+waiter.getName()+"] error.", e);
						}
					}
					break;
				}
			}
			
		} catch (Exception e) {
			String error = "Execution Market Trade CrawlerJob error.";
			log.error(error, e);
		}
		
		log.info(new StringBuilder("end[").append((System.currentTimeMillis() - start)).append("]").toString());
	}

}

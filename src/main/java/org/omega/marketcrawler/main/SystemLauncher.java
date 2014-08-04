package org.omega.marketcrawler.main;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.joda.time.DateTime;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.job.BigMarketSummaryCrawlerJob;
import org.omega.marketcrawler.job.MarketTradeCrawlerJob;
import org.omega.marketcrawler.job.RefreshCacheJob;
import org.omega.marketcrawler.job.SeekCoinJob;
import org.omega.marketcrawler.job.SmallMarketSummaryCrawlerJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public final class SystemLauncher {

	private static final Log log = LogFactory.getLog(SystemLauncher.class);
	
	private static final SystemLauncher launcher = new SystemLauncher();
	private Scheduler scheduler;
	
	private SystemLauncher() {
		
	}
	
	public static SystemLauncher getLauncher() {
		return launcher;
	}
	
	public int startup() {
		File logxml = Utils.tryGetResourceFile("log4j.xml");
		if (logxml.exists()) {
			DOMConfigurator.configure(logxml.getAbsolutePath());
			log.info("Find log4j.xml path [" + logxml.getAbsolutePath() + "].");
		} else {
			System.out.println("DO NOT find any log4j.xml");
			return 0;
		}
		
		int exitCode = 1;
		try {
			DateTime curr = new DateTime();
			scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            
            // 1 - fetch market summary
            JobDetail marketJob = newJob(SmallMarketSummaryCrawlerJob.class).withIdentity("marketjob", "marketgroup").build();
            Trigger marketTrigger = newTrigger().withIdentity("marketTri", "marketTriGrop")
  		          			.startAt(curr.plusHours(2).toDate()).withSchedule(
  		          					simpleSchedule().withIntervalInHours(2).repeatForever())
  		          			.build();
            // 2 - refresh watched item
//            JobDetail watchedItemJob = newJob(RefreshWatchedItemJob.class).withIdentity("watchedItemjob", "watchedItemgroup").build();
//            Trigger watchedItemTrigger = newTrigger().withIdentity("watchedItemTri", "watchedItemTriGrop")
//  		          			.startNow().withSchedule(
//  		          					simpleSchedule().withIntervalInMinutes(15).repeatForever())
//  		          			.build();
            // 3 - refresh cache
            JobDetail cachedPKJob = newJob(RefreshCacheJob.class).withIdentity("cachedPKjob", "cachedPKgroup").build();
            Trigger cachedPKTrigger = newTrigger().withIdentity("cachedPKTri", "cachedPKTriGrop")
  		          			.startNow().withSchedule(
  		          					simpleSchedule().withIntervalInMinutes(30).repeatForever())
  		          			.build();
            
            // 4 - fetch market trade data
            JobDetail tradeJob = newJob(MarketTradeCrawlerJob.class).withIdentity("tradejob", "tradegroup").build();
            Trigger tradeTrigger = newTrigger().withIdentity("tradeTri", "tradeTriGrop")
	            		          .startAt(curr.plusSeconds(20).toDate()).withSchedule(
	            		        		  simpleSchedule().withIntervalInSeconds(60).repeatForever())
	                              .build();
            
            // 5 - fetch big market summary
            JobDetail bigMarketJob = newJob(BigMarketSummaryCrawlerJob.class).withIdentity("bigMarketjob", "bigMarketgroup").build();
            Trigger bigMarketTrigger = newTrigger().withIdentity("bigMarketTri", "bigMarketTriGrop")
  		          			.startAt(curr.plusDays(1).toDate()).withSchedule(
  		          					simpleSchedule().withIntervalInHours(24).repeatForever())
  		          			.build();
            
            // 6 - do the statistics for all the active trade
//            JobDetail tradeStatJob = newJob(TradeStatisticsJob.class).withIdentity("tradeStatjob", "tradeStatgroup").build();
//            Trigger tradeStatTrigger = newTrigger().withIdentity("tradeStatTri", "tradeStatTriGrop")
//  		          			.startAt(curr.plusSeconds(45).toDate()).withSchedule(
//  		          					simpleSchedule().withIntervalInSeconds(60).repeatForever())
//  		          			.build();
            
            // 6
            JobDetail coinJob = newJob(SeekCoinJob.class).withIdentity("coinjob", "coingroup").build();
            Trigger coinTrigger = newTrigger().withIdentity("coinTri", "coinTriGrop")
  		          			.startAt(curr.plusHours(4).toDate()).withSchedule(
  		          					simpleSchedule().withIntervalInHours(4).repeatForever())
  		          			.build();
            
            
            
            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(marketJob, marketTrigger);
//            scheduler.scheduleJob(watchedItemJob, watchedItemTrigger);
            scheduler.scheduleJob(cachedPKJob, cachedPKTrigger);
//            
            scheduler.scheduleJob(tradeJob, tradeTrigger);
            scheduler.scheduleJob(bigMarketJob, bigMarketTrigger);
//            scheduler.scheduleJob(tradeStatJob, tradeStatTrigger);
            
            scheduler.scheduleJob(coinJob, coinTrigger);

//            scheduler.shutdown();

        } catch (Exception se) {
            log.error("start Launcher error.", se);
            exitCode = 0;
        }
		
		return exitCode;
	}
	
	public void shutdown() {
		log.info("SystemLauncher shutdown...");
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			log.error("stop Launcher error.", e);
		}
	}
	
	
	
	public static void main(String[] args) {
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
		    public void run() {
		        log.info("Shutdown hook was invoked. Shutting down App1.");
		        launcher.shutdown();
		    }
		});
		
		launcher.startup();
		
		
		
	}
	
}

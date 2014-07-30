package org.omega.marketcrawler.main;

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
import org.omega.marketcrawler.job.TradeStatisticsJob;
import org.omega.marketcrawler.net.MultiThreadedNetter;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


public final class SystemLauncher extends Thread {

	private static final Log log = LogFactory.getLog(SystemLauncher.class);
	
	private static final SystemLauncher launcher = new SystemLauncher();
	
	private SystemLauncher() {}
	
	public void run() {
		launcher.launch();
	}
	
	public void launch() {
		File logxml = Utils.tryGetResourceFile("log4j.xml");
		if (logxml.exists()) {
			DOMConfigurator.configure(logxml.getAbsolutePath());
			log.info("Find log4j.xml path [" + logxml.getAbsolutePath() + "].");
		} else {
			System.out.println("DO NOT find any log4j.xml");
			return;
		}
		
		try {
			MultiThreadedNetter.inst().reinit();
		} catch (Exception e) {
			log.error("init MultiThreadedNetter error.", e);
			return;
		}
		
		try {
			DateTime curr = new DateTime();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            
            // 1 - fetch market summary
            JobDetail marketJob = JobBuilder.newJob(SmallMarketSummaryCrawlerJob.class).withIdentity("marketjob", "marketgroup").build();
            Trigger marketTrigger = TriggerBuilder.newTrigger().withIdentity("marketTri", "marketTriGrop")
  		          			.startAt(curr.plusHours(2).toDate()).withSchedule(
  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(2).repeatForever())
  		          			.build();
            // 2 - refresh watched item
//            JobDetail watchedItemJob = JobBuilder.newJob(RefreshWatchedItemJob.class).withIdentity("watchedItemjob", "watchedItemgroup").build();
//            Trigger watchedItemTrigger = TriggerBuilder.newTrigger().withIdentity("watchedItemTri", "watchedItemTriGrop")
//  		          			.startNow().withSchedule(
//  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(15).repeatForever())
//  		          			.build();
            // 3 - refresh cache
            JobDetail cachedPKJob = JobBuilder.newJob(RefreshCacheJob.class).withIdentity("cachedPKjob", "cachedPKgroup").build();
            Trigger cachedPKTrigger = TriggerBuilder.newTrigger().withIdentity("cachedPKTri", "cachedPKTriGrop")
  		          			.startNow().withSchedule(
  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(30).repeatForever())
  		          			.build();
            
            // 4 - fetch market trade data
            JobDetail tradeJob = JobBuilder.newJob(MarketTradeCrawlerJob.class).withIdentity("tradejob", "tradegroup").build();
            Trigger tradeTrigger = TriggerBuilder.newTrigger().withIdentity("tradeTri", "tradeTriGrop")
	            		          .startAt(curr.plusSeconds(20).toDate()).withSchedule(
	            		        		  SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
	                              .build();
            
            // 5 - fetch big market summary
            JobDetail bigMarketJob = JobBuilder.newJob(BigMarketSummaryCrawlerJob.class).withIdentity("bigMarketjob", "bigMarketgroup").build();
            Trigger bigMarketTrigger = TriggerBuilder.newTrigger().withIdentity("bigMarketTri", "bigMarketTriGrop")
  		          			.startAt(curr.plusDays(1).toDate()).withSchedule(
  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).repeatForever())
  		          			.build();
            
            // 6 - do the statistics for all the active trade
//            JobDetail tradeStatJob = JobBuilder.newJob(TradeStatisticsJob.class).withIdentity("tradeStatjob", "tradeStatgroup").build();
//            Trigger tradeStatTrigger = TriggerBuilder.newTrigger().withIdentity("tradeStatTri", "tradeStatTriGrop")
//  		          			.startAt(curr.plusSeconds(45).toDate()).withSchedule(
//  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
//  		          			.build();
            
            // 6
//            JobDetail coinJob = JobBuilder.newJob(SeekCoinJob.class).withIdentity("coinjob", "coingroup").build();
//            Trigger coinTrigger = TriggerBuilder.newTrigger().withIdentity("coinTri", "coinTriGrop")
//  		          			.startAt(curr.plusHours(6).toDate()).withSchedule(
//  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(6).repeatForever())
//  		          			.build();
            
            
            
            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(marketJob, marketTrigger);
//            scheduler.scheduleJob(watchedItemJob, watchedItemTrigger);
            scheduler.scheduleJob(cachedPKJob, cachedPKTrigger);
//            
            scheduler.scheduleJob(tradeJob, tradeTrigger);
            scheduler.scheduleJob(bigMarketJob, bigMarketTrigger);
//            scheduler.scheduleJob(tradeStatJob, tradeStatTrigger);
            
//            scheduler.scheduleJob(coinJob, coinTrigger);

//            scheduler.shutdown();

        } catch (Exception se) {
            log.error("start Launcher error.", se);
        }
	}
	
	
	
	public static void main(String[] args) {
		
		launcher.start();
		
	}
	
}

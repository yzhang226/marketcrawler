package org.omega.marketcrawler.main;

import java.io.File;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.job.BigMarketSummaryCrawlerJob;
import org.omega.marketcrawler.job.MarketTradeCrawlerJob;
import org.omega.marketcrawler.job.RefreshWatchedCoinJob;
import org.omega.marketcrawler.job.SmallMarketSummaryCrawlerJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
		
		// 
//		SystemWarmup.inst().warmup();
		
		Date curr = new Date(System.currentTimeMillis());
		// 
		try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            
            // 2
            JobDetail mtcrawlerJob = JobBuilder.newJob(MarketTradeCrawlerJob.class).withIdentity("mtcjob", "mtcgroup").build();
            Trigger mtcrawlerTri = TriggerBuilder.newTrigger().withIdentity("mctTri", "mtcTriGrop")
	            		          .startAt(Utils.addSeconds(curr, 2 * 60)).withSchedule(
	            		        		  SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
	                              .build();
            
            // 2
            JobDetail rwcJob = JobBuilder.newJob(RefreshWatchedCoinJob.class).withIdentity("rwcjob", "rwcgroup").build();
            Trigger warmupTri = TriggerBuilder.newTrigger().withIdentity("rwcTri", "rwcTriGrop")
  		          			.startAt(Utils.addSeconds(curr, 1 * 60)).withSchedule(
  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(15).repeatForever())
  		          			.build();
            
            // 1
            JobDetail smscJob = JobBuilder.newJob(SmallMarketSummaryCrawlerJob.class).withIdentity("smscjob", "smscgroup").build();
            Trigger smscTri = TriggerBuilder.newTrigger().withIdentity("smscTri", "smscTriGrop")
  		          			.startNow().withSchedule(
  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1).repeatForever())
  		          			.build();
            // 4
            JobDetail bmscJob = JobBuilder.newJob(BigMarketSummaryCrawlerJob.class).withIdentity("bmscjob", "bmscgroup").build();
            Trigger bmscTri = TriggerBuilder.newTrigger().withIdentity("bmscTri", "bmscTriGrop")
  		          			.startAt(Utils.addSeconds(curr, Utils.SECONDS_ONE_DAY)).withSchedule(
  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(30).repeatForever())
  		          			.build();
            
            
            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(smscJob, smscTri);
            scheduler.scheduleJob(rwcJob, warmupTri);
            scheduler.scheduleJob(mtcrawlerJob, mtcrawlerTri);
            
            
            scheduler.scheduleJob(bmscJob, bmscTri);

//            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		
		launcher.start();
		
	}
	
}

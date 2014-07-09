package org.omega.marketcrawler.main;

import java.io.File;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.job.MarketTradeCrawlerJob;
import org.omega.marketcrawler.job.WatchNewCoinsJob;
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
		SystemWarmup.inst().warmup();
		
		Date curr = new Date(System.currentTimeMillis());
		// 
		try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            
            JobDetail mtcrawlerJob = JobBuilder.newJob(MarketTradeCrawlerJob.class).withIdentity("mtcjob", "mtcgroup").build();
            Trigger mtcrawlerTri = TriggerBuilder.newTrigger().withIdentity("mctTri", "mtcTriGrop")
	            		          .startNow().withSchedule(
	            		        		  SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
	                              .build();
            
            JobDetail warmupJob = JobBuilder.newJob(WatchNewCoinsJob.class).withIdentity("warmupjob", "warmupgroup").build();
            Trigger warmupTri = TriggerBuilder.newTrigger().withIdentity("warmupTri", "warmupTriGrop")
  		          			.startAt(Utils.addSeconds(curr, 15 * 60)).withSchedule(
  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(15).repeatForever())
  		          			.build();
            
            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(mtcrawlerJob, mtcrawlerTri);
            scheduler.scheduleJob(warmupJob, warmupTri);

//            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		
		launcher.start();
		
	}
	
}

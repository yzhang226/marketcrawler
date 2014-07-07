package org.omega.marketcrawler.main;

import org.apache.log4j.xml.DOMConfigurator;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.job.MarketTradeCrawlerJob;
import org.omega.marketcrawler.job.SystemWarmupJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


public final class SystemLauncher {

	private SystemLauncher() {}
	
	public static void main(String[] args) {
		DOMConfigurator.configure(Utils.getResourcePath("log4j.xml"));
		
		// 
		SystemWarmup.inst().warmup();
		
		// 
		try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            
            JobDetail mtcrawlerJob = JobBuilder.newJob(MarketTradeCrawlerJob.class).withIdentity("mtcjob", "mtcgroup").build();
            Trigger mtcrawlerTri = TriggerBuilder.newTrigger().withIdentity("mctTri", "mtcTriGrop")
	            		          .startNow().withSchedule(
	            		        		  SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
	                              .build();
            
            JobDetail warmupJob = JobBuilder.newJob(SystemWarmupJob.class).withIdentity("warmupjob", "warmupgroup").build();
            Trigger warmupTri = TriggerBuilder.newTrigger().withIdentity("warmupTri", "warmupTriGrop")
  		          			.startNow().withSchedule(
  		          					SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
  		          			.build();
            
            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(mtcrawlerJob, mtcrawlerTri);
            scheduler.scheduleJob(warmupJob, warmupTri);

//            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
		
	}
	
}

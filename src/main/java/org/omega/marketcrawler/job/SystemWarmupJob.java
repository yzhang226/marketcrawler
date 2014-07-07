package org.omega.marketcrawler.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.main.SystemWarmup;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SystemWarmupJob implements Job {
	
	private static final Log log = LogFactory.getLog(SystemWarmupJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start");
		
		SystemWarmup.inst().warmup();
		
		log.info("end");
	}

}

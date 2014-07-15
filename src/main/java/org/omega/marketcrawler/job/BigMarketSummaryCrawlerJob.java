package org.omega.marketcrawler.job;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.db.MarketSummaryService;
import org.omega.marketcrawler.operator.Cryptsy;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BigMarketSummaryCrawlerJob implements Job {
	
	private static final Log log = LogFactory.getLog(BigMarketSummaryCrawlerJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start refresh Big Market Summary job ...");
		
		MarketSummaryService mss = new MarketSummaryService();
		
		try {
			mss.save(Cryptsy.instance().getMarketSummaries());
		} catch (SQLException e) {
			log.error("try refresh Big Market Summary error.", e);
		}
		log.info("end refresh Big Market Summary job.");
	}

}

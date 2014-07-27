package org.omega.marketcrawler.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.operator.Cryptsy;
import org.omega.marketcrawler.service.MarketSummaryService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BigMarketSummaryCrawlerJob implements Job {
	
	private static final Log log = LogFactory.getLog(BigMarketSummaryCrawlerJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start refresh Big Market Summary job ...");
		
		MarketSummaryService mss = new MarketSummaryService();
		try {
			List<MarketSummary> summs = Cryptsy.instance().getMarketSummaries();
			if (Utils.isNotEmpty(summs)) mss.save(summs);
		} catch (Exception e) {
			String error = "try refresh Big Market Summary error.";
			log.error(error, e);
//			throw new JobExecutionException(error, e);
		}
		log.info("end refresh Big Market Summary job.");
	}

}

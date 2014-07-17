package org.omega.marketcrawler.job;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.MarketSummaryService;
import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.operator.Bittrex;
import org.omega.marketcrawler.operator.Mintpal;
import org.omega.marketcrawler.operator.Poloniex;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SmallMarketSummaryCrawlerJob implements Job {
	
	private static final Log log = LogFactory.getLog(SmallMarketSummaryCrawlerJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start refresh Small Market Summary job ...");
		
		MarketSummaryService mss = new MarketSummaryService();
		try {
			List<MarketSummary> summs = Mintpal.instance().getMarketSummaries();
			if (Utils.isNotEmpty(summs)) mss.save(summs);
			summs = Bittrex.instance().getMarketSummaries();
			if (Utils.isNotEmpty(summs)) mss.save(summs);
			summs = Poloniex.instance().getMarketSummaries();
			if (Utils.isNotEmpty(summs)) mss.save(summs);
		} catch (SQLException e) {
			log.error("try refresh Small Market Summary error.", e);
		}
		log.info("end refresh Small Market Summary job.");
	}

}

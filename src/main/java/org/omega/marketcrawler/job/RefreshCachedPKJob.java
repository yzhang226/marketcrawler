package org.omega.marketcrawler.job;

import java.sql.SQLException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.entity.WatchListItem;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RefreshCachedPKJob implements Job {
	
	private static final Log log = LogFactory.getLog(RefreshCachedPKJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		MyCache.inst().clearAllPKs();
		
		MarketTradeService mtser = new MarketTradeService();
		Set<WatchListItem> items = MyCache.inst().getWatchedItems();
		for (WatchListItem item : items) {
			try {
				MyCache.inst().getCachedPKs(item).addAll(mtser.findTopTradeTimes(item, 200));
			} catch (SQLException e) {
				log.error("add top trade_time pks to cache error.", e);
			}
		}
		
		log.info("end");
	}

}

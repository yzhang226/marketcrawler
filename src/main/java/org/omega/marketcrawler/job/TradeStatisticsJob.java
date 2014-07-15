package org.omega.marketcrawler.job;

import java.sql.SQLException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.db.TradeStatisticsService;
import org.omega.marketcrawler.entity.WatchListItem;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TradeStatisticsJob implements Job {

	private static final Log log = LogFactory.getLog(TradeStatisticsJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		Set<WatchListItem> items = MyCache.inst().getWatchedItems();
		TradeStatisticsService statSer = new TradeStatisticsService();
		for (WatchListItem item : items) {
			try {
				statSer.doOneMinuteStatistics(item, 2);
			} catch (SQLException e) {
				log.error("Do One Minute Statistics error.", e);
			}
		}
		
	}
	
	

}

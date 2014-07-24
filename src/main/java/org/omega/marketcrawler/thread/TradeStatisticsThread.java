package org.omega.marketcrawler.thread;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.db.TradeStatisticsService;
import org.omega.marketcrawler.db.WatchListItemService;
import org.omega.marketcrawler.entity.TradeStatistics;
import org.omega.marketcrawler.entity.WatchListItem;

public class TradeStatisticsThread extends Thread {

	private static final Log log = LogFactory.getLog(TradeStatisticsThread.class);
	
	private WatchListItem item;
	
	public TradeStatisticsThread(WatchListItem item) {
		this.item = item;
	}

	public void run() {
		setName(item.toReadableText());
		
		StringBuilder info = new StringBuilder("end. ");
		try {
			MarketTradeService mtser = new MarketTradeService();
			Long maxTradeMillis = mtser.getMaxTradeTime(item);
			if (maxTradeMillis == null) { return; }
			
			maxTradeMillis = Utils.getOneMinuteRangeEnd(maxTradeMillis);
			
			long currStartMillis = new DateTime(DateTimeZone.UTC).getMillis();
			currStartMillis = Utils.getOneMinuteRangeStart(currStartMillis);
			
			
			TradeStatisticsService statser = new TradeStatisticsService();
			Long maxStartMillis = statser.getMaxStartTime(item.getId());
			if (maxStartMillis == null) {// no data before
				maxStartMillis = Utils.getOneMinuteRangeStart(mtser.getMinTradeTime(item));
			}
			
			int minutes = (int) ((maxTradeMillis - maxStartMillis ) / Constants.MILLIS_ONE_MINUTE);
			
			TradeStatistics stat = statser.getByIdAndTime(item.getId(), maxStartMillis, maxTradeMillis);
			if (stat != null) {
				Long mtCount = (Long) mtser.getCountByRange(item, maxStartMillis, maxTradeMillis);
				if (mtCount != null && stat.getCount() == mtCount.intValue()) {
					log.info(info.toString());
					return ;
				}
			}
			
			int[] resu = statser.doOneMinuteStatistics(item, minutes);
			int updated = Utils.countBatchResult(resu);
			if (updated > 0) {
				info.append("Affected total " + updated + " row records.");
			}
		} catch (Exception e) {
			log.error("Do One Minute Statistics error.", e);
		}
		
		log.info(info.toString());
	}
	
	public static void main(String[] args) throws SQLException {
		// 13_bittrex_BTC_XST_Stat
//		WatchListItem item = new WatchListItem("bittrex", "KEY", "BTC");
//		item.setId(27);
		
		WatchListItemService wiser = new WatchListItemService();
		List<WatchListItem> items = wiser.findActiveItems();
		for (WatchListItem item : items) {
			new TradeStatisticsThread(item).start();
		}
		
		
	}
	
}

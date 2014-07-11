package org.omega.marketcrawler.thread;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.operator.OperatorFactory;

public class MarketTradeSpider extends Thread {
	
	private static final Log log = LogFactory.getLog(MarketTradeSpider.class);
	
	private WatchListItem item;
	
	public MarketTradeSpider(WatchListItem item) {
		this.item = item;
	}
	
	public void run() {
		setName(item.toSimpleText());
		
		long start = System.currentTimeMillis();
		MarketTradeService ser = new MarketTradeService();
		StringBuilder info = new StringBuilder();
		try {
			List<MarketTrade> records = OperatorFactory.getMarketTrades(item);
			Utils.removeRepeated(item, records);
			int[] resu = ser.save(item, records);
			
			int updated = Utils.countBatchResult(resu);
			if (updated > 0) {
				// update cache
				for (MarketTrade mt : records) {
					MyCache.inst().addPK(item, mt.getTradeTime());
				}
				info.append("Total affected " + updated + " rows number, total " + ser.getCount(item) + " records in table.");
			}
		} catch (SQLException e) {
			log.error("fetch [" + item.toReadableText() + "]'s market data error.", e);
		}
		
		info.insert(0, "end crawler, total spent time is [" + (System.currentTimeMillis() - start) + "].");
		
		log.info(info.toString());
	}

}

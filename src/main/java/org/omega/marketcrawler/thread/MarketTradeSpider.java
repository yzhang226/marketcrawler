package org.omega.marketcrawler.thread;

import java.util.Iterator;
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
		setName(item.toReadableText());
		
		long start = System.currentTimeMillis();
		MarketTradeService ser = new MarketTradeService();
		StringBuilder info = new StringBuilder();
		try {
			List<MarketTrade> records = OperatorFactory.getMarketTrades(item);
			if (Utils.isNotEmpty(records)) {
				removeRepeated(item, records);
				int[] resu = ser.save(item, records);
				
				int updated = Utils.countBatchResult(resu);
				if (updated > 0) {
					info.append("Total affected " + updated + " rows.");
				}
			}
		} catch (Throwable e) {
			log.error("fetch market data error.", e);
		}
		
		info.insert(0, "End. Spent time [" + (System.currentTimeMillis() - start) + "]. ");
		log.info(info.toString());
	}
	
	private void removeRepeated(WatchListItem item, List<MarketTrade> records) throws Exception {
		if (Utils.isEmpty(records)) return;
		
		Iterator<MarketTrade> iter = records.iterator();
		MarketTrade nxt = null;
		while (iter.hasNext()) {
			nxt = iter.next();
			if (MyCache.inst().containsKey(item, nxt.getTradeTime())) {// 
				iter.remove();
			} else {// update cache
				MyCache.inst().addPK(item, nxt.getTradeTime());
			}
		}
	}

}

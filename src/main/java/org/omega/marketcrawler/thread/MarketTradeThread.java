package org.omega.marketcrawler.thread;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.operator.OperatorFactory;
import org.omega.marketcrawler.service.MarketTradeService;

public class MarketTradeThread extends Thread {
	
	private static final Log log = LogFactory.getLog(MarketTradeThread.class);
	
	private WatchListItem item;
	
	public MarketTradeThread(WatchListItem item) {
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
				if (updated > 0) { info.append(" Add " + updated + " rows."); }
			}
		} catch (Throwable e) {
			log.error("fetch market data error.", e);
		}
		
		info.insert(0, "End[" + (System.currentTimeMillis() - start) + "].");
		log.info(info.toString());
	}
	
	private void removeRepeated(WatchListItem item, List<MarketTrade> records) throws Exception {
		if (Utils.isEmpty(records)) return;
		
		MarketTrade latest = MyCache.inst().getLatestTrade(item);
		if (latest == null) {
			MyCache.inst().putLatestTrade(item, records.get(0));
			return;
		}
		
		// 
		MarketTrade updatedLatest = null;
		
		Iterator<MarketTrade> iter = records.iterator();
		MarketTrade curr = null;
		int i = 0;
		while (iter.hasNext()) {
			i++;
			curr = iter.next();
			
			if (didTradeHappenedAfter(latest, curr)) {
				if (i == 1) { updatedLatest = curr; }
				continue;
			}
			
			iter.remove();
		}
		if (updatedLatest != null) MyCache.inst().putLatestTrade(item, updatedLatest); 
	}
	
	private boolean didTradeHappenedAfter(MarketTrade latest, MarketTrade curr) {
		boolean isAfter = false;
		if (curr.getTradeTime() == latest.getTradeTime()) {
			if (latest.getTradeId() != null && curr.getTradeId() != null && curr.getTradeId() > curr.getTradeId()) {
				isAfter = true;
			}
		} else if (curr.getTradeTime() > latest.getTradeTime()) {
			isAfter = true;
		}
		
		return isAfter;
	}
	

}

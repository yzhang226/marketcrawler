package org.omega.marketcrawler.thread;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
//		setName(item.toReadableText());
		
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
			
			TimeUnit.MILLISECONDS.sleep(50);
		} catch (Throwable e) {
			log.error("fetch market trade data[" + item.toReadableText() + "] error.", e);
		}
		
		info.insert(0, item.toReadableText() + " End[" + (System.currentTimeMillis() - start) + "].");
		log.info(info.toString());
	}
	
	private void removeRepeated(WatchListItem item, List<MarketTrade> records) throws Exception {
		if (Utils.isEmpty(records)) return;
		
		MarketTrade latest = MyCache.inst().getLatestTrade(item);
		if (latest == null || latest.getTradeTime() == 0) {
			MyCache.inst().putLatestTrade(item, records.get(0));
			return;
		}
		
		MarketTrade updatedLatest = null;
		
		Iterator<MarketTrade> iter = records.iterator();
		MarketTrade curr = null;
		while (iter.hasNext()) {
			curr = iter.next();
			
			if (didTradeHappenedAfter(latest, curr)) {
				if (updatedLatest == null || 
						didTradeHappenedAfter(updatedLatest, curr)) { 
					updatedLatest = curr; 
				}
				continue;
			}
			
			iter.remove();
		}
		if (updatedLatest != null) MyCache.inst().putLatestTrade(item, updatedLatest); 
	}
	
	private boolean didTradeHappenedAfter(MarketTrade latest, MarketTrade curr) {
		boolean isAfter = false;
		if (curr.getTradeTime() == latest.getTradeTime()) {
			if (latest.getTradeId() != null ) {// 
				if (curr.getTradeId() != null && curr.getTradeId() > latest.getTradeId()) {
					isAfter = true;
				}
			} else if (latest.getNanoTime() != null) {// 
				if (curr.getNanoTime() != null && curr.getNanoTime() > latest.getNanoTime()) {
					isAfter = true;
				}
			}
		} else if (curr.getTradeTime() > latest.getTradeTime()) {
			isAfter = true;
		}
		
		return isAfter;
	}
	

}

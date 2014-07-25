package org.omega.marketcrawler.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Arith;
import org.omega.marketcrawler.common.MyCache;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.operator.OperatorFactory;

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
				List<MarketTrade>[] arr = removeRepeated(item, records);
				
				int[] resu = ser.save(item, records);
				int updated = Utils.countBatchResult(resu);
				if (updated > 0) { info.append(" Add " + updated + " rows."); }
				
				List<MarketTrade> needToUpdate = arr[0];
				if (Utils.isNotEmpty(needToUpdate)) {
					resu = ser.update(item, needToUpdate);
					updated = Utils.countBatchResult(resu);
					if (updated > 0) { info.append(" Update " + updated + " rows."); }
				}
				List<MarketTrade> overlaps = arr[1];
				if (Utils.isNotEmpty(overlaps)) {
					resu = ser.saveOverlapTrades(item, overlaps);
					updated = Utils.countBatchResult(resu);
					if (updated > 0) { info.append(" Overlap " + updated + " rows."); }
				}
				
			}
		} catch (Throwable e) {
			log.error("fetch market data error.", e);
		}
		
		info.insert(0, "End[" + (System.currentTimeMillis() - start) + "].");
		log.info(info.toString());
	}
	
	// return - has the same key, but not the same data
	@SuppressWarnings("unchecked")
	private List<MarketTrade>[] removeRepeated(WatchListItem item, List<MarketTrade> records) throws Exception {
		if (Utils.isEmpty(records)) return null;
		
		List<MarketTrade> needToUpdate = new ArrayList<>();
		List<MarketTrade> overlaps = new ArrayList<>();
		Iterator<MarketTrade> iter = records.iterator();
		MarketTrade nxt = null;
		MarketTrade cached = null;
		MarketTrade overlaped = null;
		while (iter.hasNext()) {
			nxt = iter.next();
			
			overlaped = MyCache.inst().getOverlapedTrade(item, nxt.getTradeTime(), nxt.getTradeType());
			if (overlaped != null) {// already overlap
				iter.remove();
				continue;
			}
			
			cached = MyCache.inst().getCachedTrade(item, nxt.getTradeTime(), nxt.getTradeType());
			if (cached != null) {
				if (cached.isNotSameWith(nxt)) {// need to merge, then update
					overlaps.add(cached.copy());
					overlaps.add(nxt.copy());
					
					MarketTrade nmt = new MarketTrade();
					double totalUnits = nxt.getTotalUnits() + cached.getTotalUnits();
					double totalCost = nxt.getTotalCost() + cached.getTotalCost();
					double price = Arith.divide(totalCost, totalUnits, 8);
					nmt.setPrice(price);
					nmt.setTotalUnits(totalUnits);
					nmt.setTotalCost(totalCost);
					nmt.setTradeTime(cached.getTradeTime());
					nmt.setTradeType(cached.getTradeType());
					
					needToUpdate.add(cached);
				}
				iter.remove();
			} else {// update cache
				MyCache.inst().addMarketTrade(item, nxt);
			}
		}
		
		return new List[]{needToUpdate, overlaps};
	}

}

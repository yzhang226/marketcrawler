package org.omega.marketcrawler.common;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.db.AltCoinService;
import org.omega.marketcrawler.db.MarketSummaryService;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.exchange.Bittrex;
import org.omega.marketcrawler.exchange.Mintpal;

public final class SystemWarmup {
	
	private static final Log log = LogFactory.getLog(SystemWarmup.class);
	
	private static final SystemWarmup sys = new SystemWarmup();
	
	private SystemWarmup() {}
	
	public static SystemWarmup inst() {
		return sys;
	}
	
	public void warmup() {
		// 
		MarketSummaryService ser = new MarketSummaryService();
		try {
			ser.save(Mintpal.instance().getMarketSummaries());
			ser.save(Bittrex.instance().getMarketSummaries());
		} catch (SQLException e) {
			log.error("init market summaries error.", e);
		}
		
		// load watch list item for watched coins
		AltCoinService altSer = new AltCoinService();
		try {
			List<String> watchedSymbols = altSer.findWatchedSymbols();
			List<WatchListItem> items = ser.findWatchedItmes(watchedSymbols);
			
			ConstantPool.inst().getWatchedItmes().addAll(items);
		} catch (Exception e) {
			log.error("init watch list item error.", e);
		}
		
		
//		ConstantPool.inst().getWatchedItmes().addAll(altSer.findWatchedSymbols());
		
		
	}
	
}

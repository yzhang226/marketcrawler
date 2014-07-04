package org.omega.marketcrawler.thread;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.exchange.OperatorFactory;

public class HistoryDataFetcher extends Thread {
	
	private static final Log log = LogFactory.getLog(HistoryDataFetcher.class);
	
	private WatchListItem item;
	
	public HistoryDataFetcher(WatchListItem item) {
		this.item = item;
	}
	
	public void run() {
		long start = System.currentTimeMillis();
		log.info("start HistoryDataFetcher");
		
		try {
			MarketTradeService mtser = new MarketTradeService();
			if (!mtser.existWatchedTable(item)) {
				mtser.createWatchedTable(item);
			}
			
			List<MarketTrade> records = OperatorFactory.getMarketTrades(item);
			mtser.save(item, records);
		} catch (SQLException e) {
			log.error("fetch [" + item.toReadableText() + "]'s market data error.", e);
		}
		
		long end = System.currentTimeMillis();
		log.info("end HistoryDataFetcher, total spent time is [" + (end -start) + "].");

	}

}

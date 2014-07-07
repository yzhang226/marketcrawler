package org.omega.marketcrawler.thread;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.db.MarketTradeService;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.exchange.OperatorFactory;

public class MarketTradeSpider extends Thread {
	
	private static final Log log = LogFactory.getLog(MarketTradeSpider.class);
	
	private WatchListItem item;
	
	public MarketTradeSpider(WatchListItem item) {
		this.item = item;
	}
	
	public void run() {
		setName(item.toSimpleText() + "");
		
		long start = System.currentTimeMillis();
		log.info("start");
		
		try {
			List<MarketTrade> records = OperatorFactory.getMarketTrades(item);
			new MarketTradeService().save(item, records);
		} catch (SQLException e) {
			log.error("fetch [" + item.toReadableText() + "]'s market data error.", e);
		}
		
		long end = System.currentTimeMillis();
		log.info("end, total spent time is [" + (end -start) + "].");
	}

}

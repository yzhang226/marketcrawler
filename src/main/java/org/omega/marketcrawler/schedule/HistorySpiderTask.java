package org.omega.marketcrawler.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.exchange.Mintpal;
import org.omega.marketcrawler.thread.HistoryDataFetcher;

public class HistorySpiderTask extends TimerTask {
	
	private static final Log log = LogFactory.getLog(HistorySpiderTask.class);
	
	public void run() {
		log.info("start HistorySpiderTask");
		new HistoryDataFetcher("BC", Symbol.BTC.name(), Mintpal.NAME).start();;
	}
	
	public static void main(String[] args) {
		
		DOMConfigurator.configure(Utils.getResourcePath("log4j.xml"));
		
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTime(new Date());
//		c.set(Calendar.HOUR_OF_DAY, 14);
//		c.set(Calendar.MINUTE, 47);
//		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		long period = 1 * 60 * 1000;// 1 minute
		
		Timer timer = new Timer();
		timer.schedule(new HistorySpiderTask(), c.getTime(), period);

	}

}

package org.omega.marketcrawler.thread;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.DetailAltCoinParser;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.AltCoin;
import org.omega.marketcrawler.entity.MyTopic;
import org.omega.marketcrawler.net.MultiThreadedNetter;

public class DetailAltCoinThread implements Callable<AltCoin> {
	
	private static final Log log = LogFactory.getLog(DetailAltCoinThread.class);
	
	private MyTopic myTopic;
	
	public DetailAltCoinThread(MyTopic myTopic) {
		this.myTopic = myTopic;
	}
	
	public AltCoin call() throws Exception {
		String url = Utils.getTopicUrl(myTopic.getTopicId());
		log.info("Visit url for detail: " + url);
		
		AltCoin coin = null;
		try {
			String html = MultiThreadedNetter.inst().get(url);
			coin = new DetailAltCoinParser(html, myTopic).parse();
			coin.setTopicId(myTopic.getTopicId());
		} catch (Throwable e) {
			log.error("Visit for detail: " + url + " error.", e);
		}
		
		return coin;
	}

}

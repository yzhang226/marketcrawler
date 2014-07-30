package org.omega.marketcrawler.thread;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.AltCoinParser;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.AltCoin;
import org.omega.marketcrawler.net.MultiThreadedNetter;

public class AltCoinThread implements Callable<List<AltCoin>> {

	private static final Log log = LogFactory.getLog(AltCoinThread.class);
	
	private int boardId;
	private int pageNumber;
	
	public AltCoinThread(int boardId, int pageNumber) {
		this.boardId = boardId;
		this.pageNumber = pageNumber;
	}

	public List<AltCoin> call() throws Exception {
		
		String url = Utils.getBoardUrl(boardId, pageNumber);
		log.info("Visit url: " + url);
		
		List<AltCoin> coins = null;
		try {
			String html = MultiThreadedNetter.inst().get(url);
			coins = new AltCoinParser(html).parse();
		} catch (Throwable e) {
			log.error("Visit url: " + url + " error.", e);
		}
		return coins;
	}

}

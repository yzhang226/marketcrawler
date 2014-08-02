package org.omega.marketcrawler.thread;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.MyTopicParser;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MyTopic;
import org.omega.marketcrawler.net.MultiThreadedNetter;

public class MyTopicThread implements Callable<List<MyTopic>> {

	private static final Log log = LogFactory.getLog(MyTopicThread.class);
	
	private int boardId;
	private int pageNumber;
	
	public MyTopicThread(int boardId, int pageNumber) {
		this.boardId = boardId;
		this.pageNumber = pageNumber;
	}

	public List<MyTopic> call() throws Exception {
		String url = Utils.getBoardUrl(boardId, pageNumber);
		log.info("Visit url: " + url);
		
		List<MyTopic> topics = null;
		try {
			String html = MultiThreadedNetter.inst().getWithRetries(url);
			topics = new MyTopicParser(html).parse();
			for (MyTopic m : topics) {
				m.setBoardId((short) boardId);
			}
			
			// wait for a while
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (Throwable e) {
			log.error("Visit url: " + url + " error.", e);
		}
		return topics;
	}

}

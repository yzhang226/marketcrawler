package org.omega.marketcrawler.main;

import java.util.List;

import org.omega.marketcrawler.common.DetailAltCoinParser;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.AltCoin;
import org.omega.marketcrawler.entity.MyTopic;
import org.omega.marketcrawler.net.MultiThreadedNetter;
import org.omega.marketcrawler.service.AltCoinService;
import org.omega.marketcrawler.service.MyTopicService;

public class MakeUpRunner {

	
	public static void main(String[] args) throws Exception {
		String sql = "select * from my_topic my where my.id not in ( select a.my_topic_id from alt_coin a ) and LOWER(my.title) like '%ann%' order by my.publish_time desc";
		MyTopicService topicService = new MyTopicService();
		List<MyTopic> unDbAddTopics = topicService.find(sql);
		
		AltCoinService cser = new AltCoinService();
		for (MyTopic my : unDbAddTopics) {
			String url = Utils.getTopicUrl(my.getTopicId());
			String content = MultiThreadedNetter.inst().get(url);
			AltCoin alt = new DetailAltCoinParser(content, my).parse();
			if (alt != null) {
				System.out.println("save alt coin url : " + url);
				alt.setMyTopicId(my.getId());
				cser.save(alt);
			}
		}
		
	}
	
	
}

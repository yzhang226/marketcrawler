package org.omega.marketcrawler.job;

import static org.omega.marketcrawler.common.Constants.BOARD_ID_ANN;
import static org.omega.marketcrawler.common.Constants.MILLIS_ONE_SECOND;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.AltCoin;
import org.omega.marketcrawler.entity.MyTopic;
import org.omega.marketcrawler.net.MultiThreadedNetter;
import org.omega.marketcrawler.service.AltCoinService;
import org.omega.marketcrawler.service.MyTopicService;
import org.omega.marketcrawler.thread.DetailAltCoinThread;
import org.omega.marketcrawler.thread.MyTopicThread;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SeekCoinJob implements Job {

	private static final Log log = LogFactory.getLog(SeekCoinJob.class);
	
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start");
		
		List<MyTopic> seekedTopics = null;
		try {
			int pageNumber = Utils.extractTotalPagesNumber(MultiThreadedNetter.inst().get(Utils.getBoardUrl(BOARD_ID_ANN, 0)));
			log.info("There are total " + pageNumber + " topic pages number.");
		
			seekedTopics = fectchBoardTopics(159, pageNumber);
			
			log.info("Total " + seekedTopics.size() + " were seeked.");
			
		} catch (Exception e) {
			log.error("Seek New Coin error.", e);
		}
		
		if (Utils.isEmpty(seekedTopics)) {
			return;
		}

		AltCoinService acser = new AltCoinService();
		MyTopicService topicService = new MyTopicService();
		List<MyTopic> allDbTopics = null;
		try {
			allDbTopics = topicService.findAll();
		} catch (Exception e) {
			log.error("Find All MyTopic From DB error.", e);
		}
		
		List<MyTopic> needToUpdate = null;
		List<MyTopic> needToAdd = null;
		if (Utils.isNotEmpty(allDbTopics)) {
			needToUpdate = (List<MyTopic>) CollectionUtils.intersection(allDbTopics, seekedTopics);
			needToAdd = (List<MyTopic>) CollectionUtils.subtract(seekedTopics, allDbTopics);
		} else {
			needToAdd = seekedTopics;
		}
		
		if (Utils.isNotEmpty(needToAdd)) {// insert 
			Timestamp curr = new Timestamp(System.currentTimeMillis());
			try {
				List<AltCoin> detailCoins = fectchDetailTopic(needToAdd);
				
				Map<Integer, Integer> topicIdToMyId = new HashMap<>(needToAdd.size());
				Integer latestId = 0;
				for (MyTopic my : needToAdd) {
					try {
						my.setCreateTime((int) (curr.getTime()/MILLIS_ONE_SECOND));
						latestId = topicService.save(my);
						if (latestId != null) { 
							topicIdToMyId.put(my.getTopicId(), latestId);
							my.setId(latestId); 
						}
					} catch (Exception e) {
						log.error("Save MyTopic error.", e);
					}
				}
				
				for (AltCoin detail : detailCoins) {
					detail.setMyTopicId(topicIdToMyId.get(detail.getTopicId()));
				}
				acser.save(detailCoins);
				
				log.info("\t insert alt coin to db, total " + needToAdd.size() + " coins are inserted.");
			} catch (Exception e) {
				log.error("Add New Coins To DB error.", e);
			}
		}
		
		if (Utils.isNotEmpty(needToUpdate)) {// update 
			try {
				MyTopic finded = null;
				for (MyTopic co : needToUpdate) {
					finded = (MyTopic) CollectionUtils.find(allDbTopics, new BeanPropertyValueEqualsPredicate("topicId", co.getTopicId()));
					co.setTitle(finded.getTitle());
					co.setReplies(finded.getReplies());
					co.setViews(finded.getViews());
					co.setLastPostTime(finded.getLastPostTime());
				}
				topicService.update(needToUpdate);
				
				log.info("\t update alt coin to db, total " + needToUpdate.size() + " coins are updated.");
			} catch (Exception e) {
				log.error("Update Coins To DB error.", e);
			}
		}
		
		log.info("end");
	}
	
	
	public List<MyTopic> fectchBoardTopics(int boardId, int pageNumber) throws Exception {
		List<MyTopic> seekedTopics = new ArrayList<>(pageNumber*60);
		ExecutorService exec = Executors.newCachedThreadPool();
		List<Future<List<MyTopic>>> resu = new ArrayList<>();
		for (int i=0; i<pageNumber; i++) {
			resu.add(exec.submit(new MyTopicThread(boardId, i)));
		}
		
		List<MyTopic> re = null;
		for (Future<List<MyTopic>> futu : resu) {
			if (futu.isDone()) {
				re = futu.get();
				if (Utils.isNotEmpty(re)) { seekedTopics.addAll(re); }
            } else {
                System.out.println("MyTopicThread Future result is not yet complete");  
            }
		}
		exec.shutdown();
		
		return seekedTopics;
	}

	public List<AltCoin> fectchDetailTopic(List<MyTopic> needToAdd) throws Exception {
		List<AltCoin> coins = new ArrayList<>(needToAdd.size());
		
		ExecutorService exec = Executors.newCachedThreadPool();
		List<Future<AltCoin>> resu = new ArrayList<>();
		for (int i=0; i<needToAdd.size(); i++) {
			resu.add(exec.submit(new DetailAltCoinThread(needToAdd.get(i))));
		}
		
		List<AltCoin> detailCoins = new ArrayList<>(30);
		AltCoin re = null;
		for (Future<AltCoin> futu : resu) {
			if (futu.isDone()) {
				re = futu.get();
				if (re != null) detailCoins.add(re);
            } else {
                System.out.println("DetailAltCoinThread Future result is not yet complete");  
            }
		}
		exec.shutdown();
		
		return coins;
	}
	
	
	public static void main(String[] args) throws Exception {
//		SeekCoinJob scj = new SeekCoinJob();
//		List<AltCoin> seekedTopics = scj.fectchBoardTopics(ANNOUNCEMENTS_BOARD_URL, 1);
//		System.out.println(seekedTopics.size());
		
//		String sql = "select * from alt_coin where create_time > '2014-07-16'";
//		AltCoinService acser = new AltCoinService();
//		List<AltCoin> needToUpdate = acser.find(sql);
//		
//		try {
//			List<AltCoin> detailCoins = scj.fectchAnnTopicsByUrls(needToUpdate);
//			AltCoin detail = null;
//			for (AltCoin alt : needToUpdate) {
//				detail = (AltCoin) CollectionUtils.find(detailCoins, new BeanPropertyValueEqualsPredicate("topicId", alt.getTopicId()));
//				scj.copyProperties(alt, detail);
//			}
//			acser.update(needToUpdate);
//			
//			log.info("\t update alt coin to db, total " + needToUpdate.size() + " coins are updated.");
//		} catch (Exception e) {
//			log.error("Update Coins To DB error.", e);
//		}
		
	}
	
}

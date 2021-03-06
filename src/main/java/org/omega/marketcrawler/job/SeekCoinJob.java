package org.omega.marketcrawler.job;

import static org.omega.marketcrawler.common.Constants.BOARD_ID_ANN;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

import static org.omega.marketcrawler.common.Utils.*;

public class SeekCoinJob implements Job {

	private static final Log log = LogFactory.getLog(SeekCoinJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start");
		
		long start = System.currentTimeMillis();
		List<MyTopic> seekedTopics = null;
		try {
			String htmlContent = MultiThreadedNetter.inst().getWithRetries(getBoardUrl(BOARD_ID_ANN, 0));
			int pageNumber = Utils.extractTotalPagesNumber(htmlContent);
			log.info("There are total " + pageNumber + " topic pages number.");
		
			seekedTopics = fectchBoardTopics(BOARD_ID_ANN, pageNumber);
			
			log.info("Total " + seekedTopics.size() + " were seeked.");
		} catch (Exception e) {
			log.error("Seek New Coin error.", e);
		}
		System.out.println("used time: " + (System.currentTimeMillis() - start));
		
		if (isEmpty(seekedTopics)) {
			return;
		}

		saveOrUpdate(seekedTopics);
		
		log.info("end");
	}
	
	
	public List<MyTopic> fectchBoardTopics(int boardId, int pageNumber) throws Exception {
		List<MyTopic> seekedTopics = new ArrayList<>(pageNumber*60);
		
		ExecutorService exec = Executors.newFixedThreadPool(2);
		CompletionService<List<MyTopic>> pool = new ExecutorCompletionService<>(exec);
		for (int i=0; i<pageNumber; i++) {
			pool.submit(new MyTopicThread(boardId, i));
		}
		exec.shutdown();
		while (!exec.isTerminated()) {
			exec.awaitTermination(2, TimeUnit.SECONDS);
		}
		
		System.out.println(" end Pool fectchBoardTopics");
		
		List<MyTopic> re = null;
		
		for (int i=0; i<pageNumber; i++) {
			re = pool.take().get();
			if (isNotEmpty(re)) { seekedTopics.addAll(re); }
		}
		
		return seekedTopics;
	}

	public List<AltCoin> fectchDetailTopic(List<MyTopic> needToAdd) throws Exception {
		ExecutorService exec = Executors.newFixedThreadPool(2);
		CompletionService<AltCoin> pool = new ExecutorCompletionService<>(exec);
		for (int i=0; i<needToAdd.size(); i++) {
			pool.submit(new DetailAltCoinThread(needToAdd.get(i)));
		}
		exec.shutdown();
		while (!exec.isTerminated()) {
			exec.awaitTermination(2, TimeUnit.SECONDS);
		}
		
		System.out.println(" end Pool fectchDetailTopic");
		
		List<AltCoin> detailCoins = new ArrayList<>(30);
		AltCoin re = null;
		
		for (int i=0; i<needToAdd.size(); i++) {
			re = pool.take().get();
			if (re != null) { detailCoins.add(re); }
		}
		
		return detailCoins;
	}
	
	@SuppressWarnings("unchecked")
	public void saveOrUpdate(List<MyTopic> seekedTopics) {
		MyTopicService topicService = new MyTopicService();
		List<MyTopic> allDbTopics = null;
		try {
			allDbTopics = topicService.findAll();
		} catch (Exception e) {
			log.error("Find All MyTopic From DB error.", e);
		}
		
		List<MyTopic> needToUpdate = null;
		List<MyTopic> needToAdd = null;
		if (isNotEmpty(allDbTopics)) {
			needToUpdate = (List<MyTopic>) CollectionUtils.intersection(allDbTopics, seekedTopics);
			needToAdd = (List<MyTopic>) CollectionUtils.subtract(seekedTopics, allDbTopics);
		} else {
			needToAdd = seekedTopics;
		}
		
		if (isNotEmpty(needToAdd)) {// insert
			try {
				topicService.save(needToAdd);
				
				log.info("insert my_topic to db, total " + needToAdd.size() + " are  inserted.");
			} catch (Exception e) {
				log.error("Add MyTopics To DB error.", e);
			}
		}
		
		if (isNotEmpty(needToUpdate)) {// update 
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
				
				log.info("update my_topic to db, total " + needToUpdate.size() + " coins are updated.");
			} catch (Exception e) {
				log.error("Update MyTopics To DB error.", e);
			}
		}
		
		// search topic that are not added into alt_coin table 
		addTopicAsCoin();
		
	}
	
	private void addTopicAsCoin() {
		AltCoinService coinService = new AltCoinService();
		MyTopicService topicService = new MyTopicService();
		String sql = "select * from my_topic my where my.board_id = " + BOARD_ID_ANN + " and my.id not in ( select a.my_topic_id from alt_coin a ) and LOWER(my.title) like '%ann%'";
		try {
			List<MyTopic> needAddAsCoins = topicService.find(sql);
			if (Utils.isNotEmpty(needAddAsCoins)) {
				List<AltCoin> coins = fectchDetailTopic(needAddAsCoins);
				int[] resu1 = coinService.save(coins);
				int[] resu2 = topicService.updatePublishTime(needAddAsCoins);
				
				log.info("Add topics to coins to db, total " + Utils.countBatchResult(resu1) + " coins are inserted.");
				log.info("Add topics to coins to db, total " + Utils.countBatchResult(resu2) + " topics are updated.");
				
			}
		} catch (Exception e) {
			log.error("Add topics to coins error.", e);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		SeekCoinJob seeker = new SeekCoinJob();
//		seeker.execute(null);
		seeker.addTopicAsCoin();
	}
	
}

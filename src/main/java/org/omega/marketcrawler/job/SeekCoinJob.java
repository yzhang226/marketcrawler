package org.omega.marketcrawler.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.AltCoinParser;
import org.omega.marketcrawler.common.DetailAltCoinParser;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.AltCoinService;
import org.omega.marketcrawler.entity.AltCoin;
import org.omega.marketcrawler.net.NetUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SeekCoinJob implements Job {

	private static final Log log = LogFactory.getLog(SeekCoinJob.class);
	
//	private static final int numberOfCrawlers = 4;
	
	private static final String TOPIC_BASE_URL = "https://bitcointalk.org/index.php?topic=";
	private static final String BOARD_BASE_URL = "https://bitcointalk.org/index.php?board=";
	private static final String ANNOUNCEMENTS_BOARD_URL = BOARD_BASE_URL + "159.";
	public static final String ANN_PAGE_URL = ANNOUNCEMENTS_BOARD_URL+ "0";
	
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("start");
		
		List<AltCoin> seekedTopics = null;
		try {
			int pageNumber = Utils.extractTotalPagesNumber(NetUtils.get(ANN_PAGE_URL));
			log.info("There are total " + pageNumber + " topic pages number.");
		
			seekedTopics = fectchBoardTopics(ANNOUNCEMENTS_BOARD_URL, pageNumber);
			log.info("Total " + seekedTopics.size() + " were seeked.");
			
		} catch (Exception e) {
			log.error("Seek New Coin error.", e);
		}
		
		if (Utils.isEmpty(seekedTopics)) {
			return;
		}

		AltCoinService acser = new AltCoinService();
		List<AltCoin> allDbCoins = null;
		try {
			allDbCoins = acser.findAll();
		} catch (Exception e) {
			log.error("Find All Coins From DB error.", e);
		}
		
		List<AltCoin> needToUpdate = null;
		List<AltCoin> needToAdd = null;
		if (Utils.isNotEmpty(allDbCoins)) {
			needToUpdate = (List<AltCoin>) CollectionUtils.intersection(allDbCoins, seekedTopics);
			needToAdd = (List<AltCoin>) CollectionUtils.subtract(seekedTopics, allDbCoins);
		} else {
			needToAdd = seekedTopics;
		}
		
		if (Utils.isNotEmpty(needToAdd)) {// insert 
			Timestamp curr = new Timestamp(System.currentTimeMillis());
			try {
				List<AltCoin> detailCoins = fectchDetailTopicByUrls(needToAdd);
				AltCoin detail = null;
				for (AltCoin alt : needToAdd) {
					detail = (AltCoin) CollectionUtils.find(detailCoins, new BeanPropertyValueEqualsPredicate("topicId", alt.getTopicId()));
					copyProperties(alt, detail);
					alt.setCreateTime(curr);
				}
				acser.save(needToAdd);
				
				log.info("\t insert alt coin to db, total " + needToAdd.size() + " coins are inserted.");
			} catch (Exception e) {
				log.error("Add New Coins To DB error.", e);
			}
		}
		
		if (Utils.isNotEmpty(needToUpdate)) {// update 
			try {
				AltCoin finded = null;
				for (AltCoin co : needToUpdate) {
					finded = (AltCoin) CollectionUtils.find(seekedTopics, new BeanPropertyValueEqualsPredicate("topicId", co.getTopicId()));
					co.setTitle(finded.getTitle());
					co.setReplies(finded.getReplies());
					co.setViews(finded.getViews());
					co.setLastPostTime(finded.getLastPostTime());
				}
				acser.update(needToUpdate);
				
				log.info("\t update alt coin to db, total " + needToUpdate.size() + " coins are updated.");
			} catch (Exception e) {
				log.error("Update Coins To DB error.", e);
			}
		}
		
		log.info("end");
	}
	
	private void copyProperties(AltCoin dest, AltCoin matched) {
		dest.setPublishDate(matched.getPublishDate());
		
		dest.setName(matched.getName());
		dest.setAbbrName(matched.getAbbrName());
		
		dest.setTotalAmount(matched.getTotalAmount());
		dest.setBlockReward(matched.getBlockReward());
		dest.setBlockTime(matched.getBlockTime());
		dest.setMinedPercentage(matched.getMinedPercentage());
		
		dest.setAlgo(matched.getAlgo());
		dest.setPreMined(matched.getPreMined());

		
		String launchraw = matched.getLaunchRaw();
		if (Utils.isNotEmpty(launchraw) && launchraw.length() > 119) {
			launchraw = launchraw.substring(0, 119);
		}
		dest.setLaunchRaw(launchraw);
	}
	
	public List<AltCoin> fectchBoardTopics(String baseSeedUrl, int pageNumber) throws Exception {
		List<AltCoin> coins = new ArrayList<>(pageNumber*20);
		String url = null;
		String html = null;
		for (int i = 0; i < pageNumber; i++) {
			url =  new StringBuilder(baseSeedUrl).append(i * 40).toString();
			log.info("Visit url: " + url);
			html = NetUtils.get(url);
			coins.addAll(new AltCoinParser(html).parse());
		}
		return coins;
	}

	public List<AltCoin> fectchDetailTopicByUrls(List<AltCoin> undbAnns) throws Exception {
		List<AltCoin> coins = new ArrayList<>(undbAnns.size());
		
		int topicId;
		String url = null;
		String html = null;
		for (int i=0; i<undbAnns.size(); i++) {
			topicId = undbAnns.get(i).getTopicId();
			url = new StringBuilder(TOPIC_BASE_URL).append(topicId).append(".0").toString();
			log.info("Visit url for detail: " + url);
			html = NetUtils.get(url);
			coins.add(new DetailAltCoinParser(html, topicId).parse());
		}
		return coins;
	}
	
	
	public static void main(String[] args) throws Exception {
		SeekCoinJob scj = new SeekCoinJob();
		List<AltCoin> seekedTopics = scj.fectchBoardTopics(ANNOUNCEMENTS_BOARD_URL, 1);
		System.out.println(seekedTopics.size());
		
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

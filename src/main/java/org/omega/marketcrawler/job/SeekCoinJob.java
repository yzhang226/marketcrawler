package org.omega.marketcrawler.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.common.DocIder;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.AltCoinService;
import org.omega.marketcrawler.entity.AltCoin;
import org.omega.marketcrawler.spider.AltCoinSpider;
import org.omega.marketcrawler.spider.DetailAltCoinSpider;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.collect.Collections2;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class SeekCoinJob implements Job {

	private static final Log log = LogFactory.getLog(SeekCoinJob.class);
	
	private static final int numberOfCrawlers = 4;
	
	private static final String baseSeedUrl = "https://bitcointalk.org/index.php?board=159.";
	public static final String ANN_PAGE_URL = baseSeedUrl+ ".0";
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			int pageNumber = Utils.extractTotalPagesNumber(Utils.fetchPageByUrl(ANN_PAGE_URL));
			log.info("There are total " + pageNumber + " topic pages number.");
		
			List<AltCoin> anns = fectchAnnTopics(baseSeedUrl, pageNumber);
			
			AltCoinService acser = new AltCoinService();
			List<Long> topicIds = acser.findAllTopicIds();
			
			List<AltCoin> undbAnns = new ArrayList<>();
			List<AltCoin> dbedAnns = new ArrayList<>();
			for (AltCoin ann : anns) {
				if (!topicIds.contains(ann.getTopicId())) {
					undbAnns.add(ann);
				} else {
					dbedAnns.add(ann);
				}
			}
			anns.clear();
			
			if (Utils.isNotEmpty(undbAnns)) {// insert ann info
				Timestamp curr = new Timestamp(System.currentTimeMillis());

				List<AltCoin> detailCoins = fectchAnnTopicsByUrls(undbAnns);
				
				AltCoin detail = null;
				for (AltCoin alt : undbAnns) {
					detail = (AltCoin) CollectionUtils.find(detailCoins, new BeanPropertyValueEqualsPredicate("topicId", alt.getTopicId()));
					copyProperties(alt, detail);
					alt.setCreateTime(curr);
				}
				
				acser.save(undbAnns);
			}
			
			for (AltCoin ann : dbedAnns) {
				AltCoin alt = acser.getByTopicId(ann.getTopicId());
				alt.setTitle(ann.getTitle());
				alt.setReplies(ann.getReplies());
				alt.setViews(ann.getViews());
				alt.setLastPostTime(ann.getLastPostTime());
				
				acser.update(alt);
			}
			
			
			Thread.sleep(1 * 1000);
		} catch (Throwable e) {
			log.error("Init Ann Board By URL error.", e);
			
		}
		
	}
	
	private void copyProperties(AltCoin src, AltCoin matched) {
		src.setPublishDate(matched.getPublishDate());
		
		src.setName(matched.getName());
		src.setAbbrName(matched.getAbbrName());
		
		src.setTotalAmount(matched.getTotalAmount());
		src.setBlockReward(matched.getBlockReward());
		src.setBlockTime(matched.getBlockTime());
		src.setMinedPercentage(matched.getMinedPercentage());
		
		src.setAlgo(matched.getAlgo());
		src.setPreMined(matched.getPreMined());

		
		String launchraw = matched.getLaunchRaw();
		if (Utils.isNotEmpty(launchraw) && launchraw.length() > 119) {
			launchraw = launchraw.substring(0, 119);
		}
		src.setLaunchRaw(launchraw);
	}
	
	public List<AltCoin> fectchAnnTopics(String baseSeedUrl, int pageNumber) throws Exception {
		CrawlController controller = createCrawlController();
		
		String url = null;
		for (int i = 0; i < pageNumber; i++) {
			url = baseSeedUrl + i * 40;
			addSeed(controller, url);
		}
		controller.start(AltCoinSpider.class, 6);
		
		List<Object> data = controller.getCrawlersLocalData();
		List<AltCoin> coins = new ArrayList<>(data.size());
		for (Object obj : data) {
			coins.add((AltCoin) obj);
		}
		return coins;
	}

	public List<AltCoin> fectchAnnTopicsByUrls(List<AltCoin> undbAnns) throws Exception {
		CrawlController controller = createCrawlController();
		
		for (AltCoin alt : undbAnns) {
			addSeed(controller, alt.getLink());
		}
		controller.start(DetailAltCoinSpider.class, 2);

		List<Object> data = controller.getCrawlersLocalData();
		List<AltCoin> coins = new ArrayList<>(data.size());
		for (Object obj : data) {
			coins.add((AltCoin) obj);
		}
		return coins;
	}
	
	public CrawlController createCrawlController() throws Exception {
		CrawlConfig config = new CrawlConfig();
		
		config.setCrawlStorageFolder(Constants.CRAWL_FOLDER);
		config.setIncludeHttpsPages(true);
		config.setMaxDepthOfCrawling(0);
		config.setPolitenessDelay(1 * 1000);

		PageFetcher pageFetcher = new PageFetcher(config);
		
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		
		return new CrawlController(config, pageFetcher, robotstxtServer);
	}
	
	private void addSeed(CrawlController controller, String url) {
		int did = controller.getDocIdServer().getDocId(url);
		if (did == -1) {
			controller.addSeed(url, DocIder.getNext());
		} else {
			controller.addSeed(url, did);
		}
	}
	
}

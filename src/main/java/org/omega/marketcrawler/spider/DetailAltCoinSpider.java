package org.omega.marketcrawler.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.common.ContentMatcher;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.AltCoin;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class DetailAltCoinSpider extends WebCrawler {

	private static final Log log = LogFactory.getLog(DetailAltCoinSpider.class);
	
	private final static Pattern TALK_PATTER = Pattern.compile("^https.+bitcointalk.org.index.php.topic.\\d+\\..+$");
	private static final boolean IS_DOWNLOADING = false;
	
	private AltCoin coin;
	
	public DetailAltCoinSpider() {
		coin = new AltCoin();
	}
	
	public Object getMyLocalData() {
		return coin ;
	}
	
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return TALK_PATTER.matcher(href).matches();
	}

	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		log.info("Visit page url for detail: " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			Integer topicId = null;
			String html = null;
			try {
				topicId = Utils.getTopicIdByUrl(url);
				
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				html = htmlParseData.getHtml();
	
				HtmlCleaner cleaner = new HtmlCleaner();
				
				TagNode node = cleaner.clean(html);
				
				if (node != null) {
					String date = getPublishDate(node);
					Date postDate = null;
					if (Utils.isNotEmpty(date)) {
						// January 21, 2014, 09:01:57 PM
						// MMMMM dd, yyyy, KK:mm:ss aaa
						if (date.toLowerCase().contains("today")) {// Today at 12:39:37 AM
							postDate = Utils.parseTodayText(date);
						} else {
							postDate = Utils.parseDateText(date);
						}
						
						coin = buildAltCion(node, cleaner);
						coin.setTopicId(topicId);
						coin.setPublishDate(new Timestamp(postDate.getTime()));
					}
				}
			
			} catch (Exception e) {
				log.error("Parse html page error topic id[" + topicId + "].", e);
			}
			
			try {
				if (IS_DOWNLOADING) {
					downloadHtmlPage(coin, html);
				}
			} catch (Exception e) {
				log.error("Download page error topic id[" + topicId + "].", e);
			}
		}
	}
	
	public void downloadHtmlPage(AltCoin alt, String html) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String pdate = sdf.format(alt.getPublishDate());
		
		String fileName = pdate + "-" + alt.getTopicId() + "-" + alt.getName() + "-" + alt.getAbbrName() + ".html";
		fileName = fileName.replace("/", "").replace("\\", "");
		File htmlPath = new File(Constants.CRAWL_PAGES_FOLDER + "/" + fileName);
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(htmlPath);
			byte[] bs = html.getBytes();
			fos.write(bs);
			fos.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public String getPublishDate(TagNode page) {
		Object[] ns = null;
		try {
			ns = page.evaluateXPath("//body/div[2]/form/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr/td[2]/div[2]/span");
			if (ns == null || ns.length == 0) {
				ns = page.evaluateXPath("//body/div[2]/form/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr/td[2]/div[2]");
				
				if (ns != null && ns.length > 0) {
//					TagNode n = (TagNode) ns[0];
				} else {
					log.error("No Publish Date Node in html page.");
				}
			}
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		
		String cont = "";
		if (ns != null && ns.length > 0) {
			TagNode n = (TagNode) ns[0];
			cont = n.getText().toString();
		}
		
		return cont;
	}
	
	public String getContentHtml(TagNode page, HtmlCleaner cleaner) {
		Object[] ns = null;
		try {
			ns = page.evaluateXPath("//body/div[2]/form/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr/td[2]/div[@class='post']");
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		
		String prettyCont = "";
		if (ns != null && ns.length > 0) {
			TagNode n = (TagNode) ns[0];
			String cont = cleaner.getInnerHtml(n);
			
			PrettyHtmlSerializer htmlSer = new PrettyHtmlSerializer(cleaner.getProperties(), " ");
			prettyCont = htmlSer.getAsString(cont);
		}
		
		return prettyCont;
	}
	
	public String getTitle(TagNode page, HtmlCleaner cleaner) {
		Object[] ns = null;
		try {// //*[@id="top_subject"] 
			ns = page.evaluateXPath("//title");
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		
		String cont = "";
		if (ns != null && ns.length > 0) {
			TagNode n = (TagNode) ns[0];
			cont = cleaner.getInnerHtml(n);
		}
		
		return cont;
	}

	public AltCoin buildAltCion(TagNode page, HtmlCleaner cleaner) {
		String content = getContentHtml(page, cleaner);
		String title = getTitle(page, cleaner);
		
		String[] lineArr = content.toLowerCase().split("<br />");
		List<String> lines = new ArrayList<String>(lineArr.length);
		for (String l : lineArr) {
			lines.add(cleaner.clean(l).getText().toString());
		}

		ContentMatcher cm = new ContentMatcher(title, lines);
		AltCoin alt = cm.buildAndMatch();
		
		return alt;
	}
	
}
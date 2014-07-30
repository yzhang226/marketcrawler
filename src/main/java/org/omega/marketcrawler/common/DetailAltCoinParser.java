package org.omega.marketcrawler.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.omega.marketcrawler.entity.AltCoin;
import org.omega.marketcrawler.entity.MyTopic;

public class DetailAltCoinParser {

	private static final Log log = LogFactory.getLog(DetailAltCoinParser.class);
	
	private static final boolean IS_DOWNLOADING = false;
	
	private String content;
	private MyTopic myTopic;
	
	public DetailAltCoinParser(String content, MyTopic myTopic) {
		this.content = content;
		this.myTopic = myTopic;
	}
	
	public AltCoin parse() {
		AltCoin coin = null;
		try {
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode node = cleaner.clean(content);
			if (node != null) {
				String date = getPublishDate(node);
				Date postDate = null;
				if (Utils.isNotEmpty(date)) {
					coin = new AltCoin();
					// January 21, 2014, 09:01:57 PM
					// MMMMM dd, yyyy, KK:mm:ss aaa
					if (date.toLowerCase().contains("today")) {// Today at 12:39:37 AM
						postDate = Utils.parseTodayText(date);
					} else {
						postDate = Utils.parseDateText(date);
					}
					
					coin = buildAltCion(node, cleaner);
					myTopic.setPublishTime(Utils.changeMillsToSeconds(postDate.getTime()));
				}
			}
		
		} catch (Exception e) {
			log.error("Parse html page error topic id[" + myTopic.getTopicId() + "].", e);
		}
		
		try {
			if (IS_DOWNLOADING) {
				downloadHtmlPage(coin, content);
			}
		} catch (Exception e) {
			log.error("Download page error topic id[" + myTopic.getTopicId() + "].", e);
		}
		
		return coin;
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

	public void downloadHtmlPage(AltCoin alt, String html) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		String pdate = sdf.format(alt.getPublishDate());
//		
//		String fileName = pdate + "-" + alt.getTopicId() + "-" + alt.getName() + "-" + alt.getAbbrName() + ".html";
//		fileName = fileName.replace("/", "").replace("\\", "");
//		File htmlPath = new File(Constants.CRAWL_PAGES_FOLDER + "/" + fileName);
//		
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(htmlPath);
//			byte[] bs = html.getBytes();
//			fos.write(bs);
//			fos.flush();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (fos != null) fos.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
	}
	
}

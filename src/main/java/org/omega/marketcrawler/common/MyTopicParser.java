package org.omega.marketcrawler.common;

import static org.omega.marketcrawler.common.Constants.MILLIS_ONE_SECOND;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.omega.marketcrawler.entity.MyTopic;

public class MyTopicParser {

	private static final Log log = LogFactory.getLog(MyTopicParser.class);
	
	private String content;
	
	public MyTopicParser(String content) {
		this.content = content;
	}
	
	public List<MyTopic> parse() {
		List<MyTopic> cos = new ArrayList<>(60);
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node = cleaner.clean(content);

		Object[] ns = null;
		try {
			ns = node.evaluateXPath("//body/div[2]/div[2]/table/tbody/tr");
		} catch (XPatherException e) {
			log.error("", e);
		}
		
		
		if (ns != null && ns.length > 0) {
			for (Object obj : ns) {
				try {
					TagNode n = (TagNode) obj;
					List<TagNode> cr = n.getChildTagList();

					TagNode topicNode = null;
					try {
						Object[] ns2 = cr.get(2).evaluateXPath("//span/a");
						if (ns2 != null && ns2.length > 0) {
							topicNode = (TagNode) ns2[0];
						}
					} catch (XPatherException e) {
						e.printStackTrace();
					}

					if (topicNode != null) {
						String topicTitle = topicNode.getText().toString().trim();

						if (topicTitle.toLowerCase().contains("ann")) {
							MyTopic bean = new MyTopic();
							String link = topicNode.getAttributeByName("href");

							bean.setTitle(topicTitle);
//							bean.setLink(link);
							bean.setTopicId(Utils.getTopicIdByUrl(link));

							TagNode authorNode = (TagNode) cr.get(3).getChildTagList().get(0);
							bean.setAuthor(authorNode.getText().toString().trim());

							String replies = cr.get(4).getText().toString().trim();
							if (Utils.isNotEmpty(replies)) bean.setReplies(Integer.valueOf(replies));

							if (cr.size() > 5) {
								String views = cr.get(5).getText().toString().trim();
								if (Utils.isNotEmpty(views)) bean.setViews(Integer.valueOf(views));
							}
							
							if (cr.size() > 6) {
								String lastPostTxt = cr.get(6).getText().toString().trim();
								Date lastPost = null;
								if (Utils.isNotEmpty(lastPostTxt)) {
									String[] ss = lastPostTxt.split("\n");
									if (ss.length > 0 && Utils.isNotEmpty(ss[0])) {
										lastPostTxt =  ss[0].trim() ;
										if (lastPostTxt.toLowerCase().contains("today")) {// Today at 12:39:37 AM
											lastPost = Utils.parseTodayText(lastPostTxt);
										} else {
											lastPost = Utils.parseDateText(lastPostTxt);
										}
									}
								}
								
								if (lastPost != null) bean.setLastPostTime((int) (lastPost.getTime()/MILLIS_ONE_SECOND));
							}
							
							cos.add(bean);
						}
					}
				} catch (Exception e) {
					log.error("extract topic node error.", e);
				}

			}
		}

		return cos;
	}
	
	
}

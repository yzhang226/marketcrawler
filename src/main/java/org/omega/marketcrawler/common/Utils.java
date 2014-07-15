package org.omega.marketcrawler.common;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;

public final class Utils {

	private static final Log log = LogFactory.getLog(Utils.class);
	
	public static final String TIME_ZONE_LOCAL = TimeZone.getDefault().getID();
	public static final String TIME_ZONE_GMT = "GMT";
	
	private static final Locale LOCALE_US = Locale.US;
	private static final Pattern TODAY_DATE_PATTERN = Pattern.compile("(\\d{2}+):(\\d{2}+):(\\d{2}+) (\\w{2}+)");
	
	public static final String DATE_FORMAT_FULL = "yyyyMMddHHmmss";
	public static final String DATE_FORMAT_SHORT = "yyMMddHH";
	
	public static final long SECONDS_ONE_HOUR = 60 * 60;
	public static final long SECONDS_ONE_DAY = 24 * SECONDS_ONE_HOUR;

	public static boolean isEmpty(String text) {
		return text == null || text.trim().length() == 0;
	}
	
	public static boolean isNotEmpty(String text) {
		return !isEmpty(text);
	}
	
	public static boolean isNotEmpty(Object[] objs) {
		return objs != null && objs.length != 0;
	}
	
	public static boolean isEmpty(Collection<?> list) {
		return list == null || list.isEmpty();
	}
	
	public static boolean isNotEmpty(Collection<?> list) {
		return !isEmpty(list);
	}
	
	public static String removeChars(String s) {
		return  replaceAll(s, "\\D+", "");
	}
	
	public static String replaceAll(String s, String regex, String replacement) {
		if (s == null) {
			return "";
		}
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(s);
		
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	public static String getResourcePath(String resource) {
		URL url = Utils.class.getClassLoader().getResource(resource);
		return url == null ? "" : url.getPath();
	}
	
	public static File tryGetResourceFile(String resource) {
		String resourcePath = getResourcePath(resource);
		File resourceFile = new File(resourcePath);
		if (Utils.isEmpty(resourcePath) || !resourceFile.exists()) {
			resourceFile = new File(resource);
		}
		
		return resourceFile;
	}
	
	public static int countBatchResult(int [] resu) {
		if (resu == null || resu.length == 0) return 0;
		int total = 0;
		for (int re : resu) {
			if (re > 0) total = total + re;
		}
		
		return total;
	}
	
	public static Integer getTopicIdByUrl(String link) {
		return Integer.valueOf(link.substring(link.indexOf('=') + 1, link.lastIndexOf('.')));
	}
	
	public static Date parseDateText(String dateText) {
		Date postDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy, hh:mm:ss aaa", LOCALE_US);
		try {
			postDate = sdf.parse(dateText);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return postDate;
	}
	
	public static Date parseTodayText(String dateText) {
		Date today = new Date(System.currentTimeMillis());
		
		Date gmtToday = convertDateZone(today, TIME_ZONE_LOCAL, TIME_ZONE_GMT);
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTime(gmtToday);
		
		Matcher m = TODAY_DATE_PATTERN.matcher(dateText);
		
		Date postDate = null;
		if (m.find()) {
			int hh = Integer.valueOf(m.group(1));
			int mm = Integer.valueOf(m.group(2));
			int ss = Integer.valueOf(m.group(3));
			
			String amorpm = m.group(4);
			if (amorpm.equalsIgnoreCase("PM")) { hh = hh + 12; }
			
			cal.set(Calendar.HOUR_OF_DAY, hh);
			cal.set(Calendar.MINUTE, mm);
			cal.set(Calendar.SECOND, ss);
			
			postDate = cal.getTime();
		}
		
		return postDate;
	}
	
	public static Date convertDateZone(Date sourceDate, String srcTimeZone, String destTimeZone) {
//		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_FULL);
		TimeZone srcZone = TimeZone.getTimeZone(srcTimeZone);
		TimeZone destZone = TimeZone.getTimeZone(destTimeZone);
		long targetTime = sourceDate.getTime() - srcZone.getRawOffset() + destZone.getRawOffset();
		return new Date(targetTime);
	}
	
	
	public static long getOneMinuteRangeEnd(long baseMillis) {
		return getRangeEndMillis(baseMillis, 1);
	}
	
	public static long getOneMinuteRangeStart(long baseMillis) {
		return getRangeStartMillis(baseMillis, 1);
	}
	
	public static long getRangeEndMillis(long baseMillis, int interval) {
		DateTime base = new DateTime(baseMillis, DateTimeZone.UTC);// 
		if (interval == 1) {
			int baseSec = base.getSecondOfMinute();
			if (baseSec != 0) {
				base = base.withMillisOfSecond(0).plusSeconds(60 - baseSec);
			}
		} else {
			int baseMinute = base.getMinuteOfHour();
			if (baseMinute != 0) {
				int mod = baseMinute % interval;
				int left = mod == 0 ? 0 : interval - mod;
				base = base.withSecondOfMinute(0).withMillisOfSecond(0).plusMinutes(left);
			}
		}
		
		return base.getMillis();
	}
	
	public static long getRangeStartMillis(long baseMillis, int interval) {
		DateTime base = new DateTime(baseMillis, DateTimeZone.UTC);// 
		if (interval == 1) {
			int baseSec = base.getSecondOfMinute();
			if (baseSec == 0) {
				base = base.withMillisOfSecond(0).minusMinutes(1);
			} else {
				base = base.withMillisOfSecond(0).minusSeconds(baseSec);
			}
		} else {
			int baseMinute = base.getMinuteOfHour();
			if (baseMinute == 0) {
				base = base.withSecondOfMinute(0).withMillisOfSecond(0).minusMinutes(interval);
			} else {
				int mod = baseMinute % interval;
				int left = mod == 0 ? 0 : interval - mod;
				base = base.withSecondOfMinute(0).withMillisOfSecond(0).minusMinutes(left);
			}
		}
		
		return base.getMillis();
	}
	
	
	public static String fetchPageByUrl(String pageUrl) {
		String canonicalUrl = URLCanonicalizer.getCanonicalURL(pageUrl);
		WebURL webUrl = new WebURL();
		webUrl.setURL(canonicalUrl);
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(Constants.CRAWL_FOLDER);
		config.setIncludeHttpsPages(true);
		config.setMaxDepthOfCrawling(0);
		config.setPolitenessDelay(1 * 1000);

		PageFetcher pageFetcher = createPageFetcher();
		
		edu.uci.ics.crawler4j.crawler.Page page = new edu.uci.ics.crawler4j.crawler.Page(webUrl);
		
		PageFetchResult fetchResult = pageFetcher.fetchHeader(webUrl);
		
		if (!fetchResult.fetchContent(page)) {
			return null;
		}
		
		String content = null;
		try {
			if (page.getContentCharset() == null) {
				content = new String(page.getContentData());
			} else {
				content = new String(page.getContentData(), page.getContentCharset());
			}
		} catch (Exception e) {
			log.error("get page content error", e);
		}
		
		return content;
	}
	
	private static PageFetcher createPageFetcher() {
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(Constants.CRAWL_FOLDER);
		config.setIncludeHttpsPages(true);
		config.setMaxDepthOfCrawling(0);
		config.setPolitenessDelay(1 * 1000);

		return new PageFetcher(config);
	}
	
	public static int extractTotalPagesNumber(String html) {
		
		if (isEmpty(html)) {
			return 100;
		}
		
		HtmlCleaner cleaner = new HtmlCleaner();

		TagNode node = cleaner.clean(html);

		Object[] nodes = null;
		try {
			nodes = node.evaluateXPath("//body/div[2]/table/tbody/tr/td/a");
		} catch (XPatherException e) {
			e.printStackTrace();
		}

		if (Utils.isNotEmpty(nodes)) {
			TagNode n = (TagNode) nodes[nodes.length-1];
			return Integer.valueOf(n.getText().toString().trim()).intValue();
		}
		
		return 99;
	}
	
	public static void main(String[] args) {
		
//		for (int i=0; i<60; i++) {
//			DateTime dt = new DateTime();
//			System.out.println(dt);
//			System.out.println(dt.getMinuteOfHour());
//			System.out.println(dt.getSecondOfMinute());
//			try {
//				Thread.sleep(999);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		long sysmillis = System.currentTimeMillis();
		Date d = new Date(sysmillis);
		DateTime curr = new DateTime(sysmillis);
		DateTime curr2 = new DateTime(sysmillis, DateTimeZone.UTC);
		
		System.out.println("sysmillis: " + sysmillis);
		System.out.println("d        : " + d);
		System.out.println("curr     : " + curr);
		System.out.println("curr2    : " + curr2);
		
		System.out.println("start    : " + Utils.getRangeStartMillis(sysmillis, 1));
		System.out.println("end      : " + Utils.getRangeEndMillis(sysmillis, 1));
		System.out.println("start    : " + new DateTime(Utils.getRangeStartMillis(sysmillis, 1)));
		System.out.println("end      : " + new DateTime(Utils.getRangeEndMillis(sysmillis, 1)));
		System.out.println("start    : " + new DateTime(Utils.getRangeStartMillis(sysmillis, 1), DateTimeZone.UTC));
		System.out.println("end      : " + new DateTime(Utils.getRangeEndMillis(sysmillis, 1), DateTimeZone.UTC));
		
		
	}
	
	
}

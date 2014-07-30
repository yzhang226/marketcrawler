package org.omega.marketcrawler.common;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import static org.omega.marketcrawler.common.Constants.*;

public final class Utils {

//	private static final Log log = LogFactory.getLog(Utils.class);
	
	public static final String TIME_ZONE_LOCAL = TimeZone.getDefault().getID();
	public static final String TIME_ZONE_GMT = "GMT";
	
	private static final Locale LOCALE_US = Locale.US;
	private static final Pattern TODAY_DATE_PATTERN = Pattern.compile("(\\d{2}+):(\\d{2}+):(\\d{2}+) (\\w{2}+)");
	
	public static final String DATE_FORMAT_FULL = "yyyyMMddHHmmss";
	public static final String DATE_FORMAT_SHORT = "yyMMddHH";
	
	public static final long SECONDS_ONE_HOUR = 60 * 60;
	public static final long SECONDS_ONE_DAY = 24 * SECONDS_ONE_HOUR;
	
	private static final String TOPIC_BASE_URL = "https://bitcointalk.org/index.php?topic=";
	private static final String BOARD_BASE_URL = "https://bitcointalk.org/index.php?board=";

	public static String getTopicUrl(int topicId) {
		return new StringBuilder(TOPIC_BASE_URL).append(topicId).append(".0").toString();
	}
	
	public static String getBoardUrl(int boardId, int pageNumber) {
		return new StringBuilder(BOARD_BASE_URL).append(boardId).append(".").append(pageNumber * 40).toString();
	}
	
	public static int changeMillsToSeconds(long mills) {
		return (int) (mills/MILLIS_ONE_SECOND);
	}
	
	public static boolean isEmpty(String text) {
		return text == null || text.trim().length() == 0;
	}
	
	public static boolean isNotEmpty(String text) {
		return !isEmpty(text);
	}
	
	public static boolean isEmptyOrNull(String text) {
		return isEmpty(text) || (text.trim().length() == 4 && Constants.TEXT_NULL.equals(text.trim().toLowerCase()));
	}
	
	public static boolean isEmpty(Object[] objs) {
		return objs == null || objs.length == 0;
	}
	
	public static boolean isNotEmpty(Object[] objs) {
		return !isEmpty(objs);
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
	
	public static String right8Pad(float f) {
		DecimalFormat df = new DecimalFormat("###0.00000000");
		return df.format(f);
//		String fs = Float.toString(f);
//		//1.065
//		// 5 - 1 = 4
//		int dotIdx = fs.indexOf('.');
////		int rightLen = 8 - (fs.length() - fs.indexOf('.') - 1);
//		return fs.substring(0, dotIdx+1) + StringUtils.rightPad(fs.substring(dotIdx+1), 8, '0');
	}
	
	public static String right8Pad(double f) {
		DecimalFormat df = new DecimalFormat("#######0.00000000");
		return df.format(f);
	}
	
	public static String justFormat(double dou) {
		DecimalFormat df = new DecimalFormat("########.########");
		return df.format(dou);
	}
	
	public static Date convertDateZone(Date sourceDate, String srcTimeZone, String destTimeZone) {
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
			baseSec = baseSec == 0 ? 0 : 60 - baseSec;
			base = base.plusSeconds(baseSec);
		} else {
			base = base.withSecondOfMinute(0);
			
			int leftMinute = base.getMinuteOfHour();
			int mod = leftMinute % interval;
			leftMinute = mod == 0 ? 0 : interval - mod;
			base = base.plusMinutes(leftMinute);
		}
		
		return base.withMillisOfSecond(0).getMillis();
	}
	
	public static long getRangeStartMillis(long baseMillis, int interval) {
		DateTime base = new DateTime(baseMillis, DateTimeZone.UTC);// 
		if (interval == 1) {
			int baseSec = base.getSecondOfMinute();
			baseSec = baseSec == 0 ? 60 : baseSec;
			base = base.minusSeconds(baseSec);
		} else {
			base = base.withSecondOfMinute(0);
			
			int baseMinute = base.getMinuteOfHour();
			baseMinute = baseMinute == 0 ? interval : baseMinute % interval;
			base = base.minusMinutes(baseMinute);
		}
		
		return base.withMillisOfSecond(0).getMillis();
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
		
		System.out.println(0 % 5);
		
		System.out.println(right8Pad(222221.546f));
		System.out.println(right8Pad(0.546f));
		System.out.println(right8Pad(0.00370002f));
		System.out.println(right8Pad(0.0000021f));
		
		double dou = 0.00075;
		DecimalFormat df = new DecimalFormat("####.########");
		System.out.println(df.format(dou));
	}
	
	
}

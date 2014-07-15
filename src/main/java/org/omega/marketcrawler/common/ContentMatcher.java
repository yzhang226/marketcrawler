package org.omega.marketcrawler.common;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.entity.AltCoin;


public final class ContentMatcher {
	
	private static final Log log = LogFactory.getLog(ContentMatcher.class);
	
	private static final String DIGITS_REGEX = "([1-9]((\\d{1,3}\\D){1,5}|(\\d{1,15})))";
	
	private static final Pattern PATTERN_ABBR = Pattern.compile("\\[[^\\]]+\\][^\\[]*\\[([^\\]]+)\\]", Pattern.CASE_INSENSITIVE);
	private static final Pattern PATTERN_NAME = Pattern.compile("\\s?(\\w+\\s?coin)", Pattern.CASE_INSENSITIVE);

	private static final List<Pattern> PATTERN_TOTALs = new ArrayList<Pattern>();
	private static final List<Pattern> PATTERN_REWARDs = new ArrayList<Pattern>();
	private static final List<Pattern> PATTERN_BTIMEs = new ArrayList<Pattern>();
	private static final List<Pattern> PATTERN_PREMINEs = new ArrayList<Pattern>();
	private static final List<Pattern> PATTERN_ALGOs = new ArrayList<Pattern>();
	private static final List<Pattern> PATTERN_PREAMOUNTs = new ArrayList<Pattern>();
	private static final List<Pattern> PATTERN_LAUNCHs = new ArrayList<Pattern>();
	
	static {
		loadPatterns();
	}
	
	private static void loadPatterns() {
		String path = null;
		try {
			path = Utils.getResourcePath("patterns.properties");
			InputStream is = new FileInputStream(path);
			Properties pros = new Properties();
			pros.load(is);
			is.close();
			
			String digRegex = pros.getProperty("DIGITS_REGEX");
			for (Entry<Object, Object> entry : pros.entrySet()) {
				String value = entry.getValue().toString();
				value = value.replace("${DIGITS_REGEX}", digRegex);
				
				pros.setProperty(entry.getKey().toString(), value);
//				System.out.println(pros.get(entry.getKey().toString()));
			}
			
			addPatterns(pros.getProperty("alt.total"), PATTERN_TOTALs);
			addPatterns(pros.getProperty("alt.reward"), PATTERN_REWARDs);
			addPatterns(pros.getProperty("alt.btime"), PATTERN_BTIMEs);
			addPatterns(pros.getProperty("alt.premine.percentage"), PATTERN_PREMINEs);
			addPatterns(pros.getProperty("alt.algo"), PATTERN_ALGOs);
			addPatterns(pros.getProperty("alt.premine.amount"), PATTERN_PREAMOUNTs);
			addPatterns(pros.getProperty("alt.launch.date"), PATTERN_LAUNCHs);
		} catch (Exception e) {
			log.error("load patterns error.", e);
		}
	}
	
	private static void addPatterns(String patts, List<Pattern> ll) {
		String[] pattArr = patts.split(" , ");
		for (String patt : pattArr) {
			ll.add(Pattern.compile(patt.trim(), Pattern.CASE_INSENSITIVE));
		}
	}
	
	private String title;
	private List<String> lines = null;
	public ContentMatcher(String title, List<String> lines) {
		this.title = title;// toLowerCase()
		this.lines = lines;
	}
	
	public AltCoin buildAndMatch() {
		AltCoin alt = new AltCoin();
		
		alt.setName(buildAltName());
		alt.setAbbrName(buildAltAbbr());
		
		Long mtotal = null; 
		for (String line : lines) {
			mtotal = buildAltTotal(line);
			if (mtotal != null) {
				alt.setTotalAmount(mtotal); 
				break;
			}
		}
		
		Double mreward = null;
		for (String line : lines) {
			mreward = buildAltReward(line);
			if (mreward != null) {
				alt.setBlockReward(mreward);
				break;
			}
		}
		
		Integer mbtime = null; 
		for (String line : lines) {
			mbtime = buildAltBTime(line);
			if (mbtime != null) {
				alt.setBlockTime(mbtime);
				break;
			}
		}
		
		Double mpremine = null;
		for (String line : lines) {
			mpremine = buildAltPremine(line);
			if (mpremine != null) {
				alt.setMinedPercentage(mpremine);
				break;
			}
		}
		
		String malgo = null;
		for (String line : lines) {
			malgo = buildAltAlgo(line);
			if (malgo != null) {
				alt.setAlgo(malgo);
				break;
			}
		}
		
		Long mpreAmount = null; 
		for (String line : lines) {
			mpreAmount = buildAltPreAmount(line);
			if (mpreAmount != null) {
				alt.setPreMined(mpreAmount);
				break;
			}
		}
		
		String mlaunch = null;
		for (String line : lines) {
			mlaunch = buildAltLaunch(line);
			if (mlaunch != null) {
				if (mlaunch.length() > 50) {
					mlaunch = mlaunch.substring(0, 50);
				}
				alt.setLaunchRaw(mlaunch);
				break;
			}
		}
		
		return alt;
	}
	
	private String buildAltAbbr() {
		String abbr = "NA";
		try {
			Matcher m = PATTERN_ABBR.matcher(title);
			if (m.find()) {
				abbr = m.group(1);
			}
			if (abbr.length() > 20) {
				abbr = abbr.substring(0, 10);
			}
		} catch (Exception e) {
			log.error("Parse abbr name error", e);
		}
		return abbr.toUpperCase();
	}
	
	private String buildAltName() {
		String name = "NA";
		try {
			Matcher m = PATTERN_NAME.matcher(title);
			if (m.find()) {
				name = m.group(1);
				StringBuilder nb = new StringBuilder(name.length());
				nb.append(name.substring(0, 1).toUpperCase());
				nb.append(name.substring(1, name.length()-4));
				nb.append("Coin");
				
				name = Utils.replaceAll(nb.toString(), "\\s", "");
			}
			
			if (name.length() > 20) {
				name = name.substring(0, 19);
			}
		} catch (Exception e) {
			log.error("Parse name error", e);
		}
		return name;
	}
	
	private Long buildAltTotal(String line) {
		Long ttx = null;
		
		try {
			String total = null;
			String unit = null;
			for (Pattern p : PATTERN_TOTALs) {
				Matcher m = p.matcher(line);
				if (m.find()) {
					total = Utils.removeChars(m.group(1));
//					for (int i=1; i< m.groupCount(); i++) {
//						System.out.println(i + ", " + m.group(i));
//					}
					if (m.groupCount() >= 4) unit = m.group(4);
					break;
				}
			}
			
			ttx = Utils.isNotEmpty(total) ? Long.parseLong(total) : null;
			if (ttx != null && unit != null) {
				if (unit.toLowerCase().contains("million")) ttx = ttx * 1000000;
			}
		} catch (Exception e) {
			log.error("Parse total error", e);
		}
		return ttx;
	}
	
	private Double buildAltReward(String line) {
		Double rewardd = null;
		try {
			String reward = null;
			for (Pattern p : PATTERN_REWARDs) {
				Matcher m = p.matcher(line);
				if (m.find()) {
//					reward = m.group(1);
//					reward = reward.trim();
					reward = Utils.removeChars(m.group(1));
					break;
				}
			}
			rewardd = Utils.isNotEmpty(reward) ? Double.parseDouble(reward) : null;
		} catch (Exception e) {
			log.error("Parse block reward error", e);
		}
		
		return rewardd;
	}
	
	private Integer buildAltBTime(String line) {
		int bt = 0;
		try {
			String btime = null;
			String unit = null;
			for (Pattern p : PATTERN_BTIMEs) {
				Matcher m = p.matcher(line);
				if (m.find()) {
					btime = m.group(1);
					unit = m.group(4);
					btime = btime.trim();
					break;
				}
			}
			
			if (btime != null) {
				double btx = Double.parseDouble(btime);
				if (unit != null && unit.contains("minute")) {
					btx = btx * 60;
				}
				bt = (int) btx;
			}
		} catch (Exception e) {
			log.error("Parse block time error", e);
		}
		
		return bt == 0 ? null : bt;
	}
	
	private Double buildAltPremine(String line) {
		String pre = null;
		try {
			for (Pattern p : PATTERN_PREMINEs) {
				Matcher m = p.matcher(line);
				if (m.find()) {
					pre = m.group(1).trim();
					break;
				}
			}
		} catch (Exception e) {
			log.error("Parse premine percentage error", e);
		}
		return pre == null ? null : Double.parseDouble(pre);
	}
	
	private String buildAltAlgo(String line) {
		String algo = null;
		try {
			for (Pattern p : PATTERN_ALGOs) {
				Matcher m = p.matcher(line);
				if (m.find()) {
					algo = m.group(1).trim();
					break;
				}
			}
		} catch (Exception e) {
			log.error("Parse premine amount error", e);
		}
		return algo;
	}
	
	private Long buildAltPreAmount(String line) {
		String preAmount = null;
		try {
			for (Pattern p : PATTERN_PREAMOUNTs) {
				Matcher m = p.matcher(line);
				if (m.find()) {
					preAmount = m.group(1).trim();
					preAmount = Utils.removeChars(preAmount);
					break;
				}
			}
		} catch (Exception e) {
			log.error("Parse premine amount error", e);
		}
		return preAmount == null ? null : Long.parseLong(preAmount);
	}
	
	private String buildAltLaunch(String line) {
		String launch = null;
		try {
			for (Pattern p : PATTERN_LAUNCHs) {
				Matcher m = p.matcher(line);
				if (m.find()) {
					launch = m.group(1).trim();
					break;
				}
			}
		} catch (Exception e) {
			log.error("Parse launch date error", e);
		}
		return launch;
	}
	
	
	public static void main(String[] args) throws Exception {

//		HtmlPageMatcher pm = new HtmlPageMatcher();
		
//		File dir = new File(Constants.CRAWL_PAGES_FOLDER);
//		for (File f : dir.listFiles()) {
//			pm.tryMatch(f.getAbsolutePath());
//		}
		// "/Users/cook/Downloads/YinYangcoin.html"
//		String ff = "/storage/crawler4j/pages/20140525001628-624041-XGenerationCoin-NA.html";
//		pm.tryMatch(ff);
		// ((\d+\D?)+)\s?(coins)\s?(per|one|each)\D+block
		
		ContentMatcher cm = new ContentMatcher(null, null);
//		System.out.println(cm.buildAltTotal("Total Coins:5,000,000  "));;
//		System.out.println(cm.buildAltTotal("Total coin: 1,000,000,000(1billion)"));
		System.out.println(cm.buildAltTotal("Maximum Supply: ~ 100 Million [Mathematically Regulated with slight Inflation]"));
		System.out.println(cm.buildAltTotal(" Total POW Coins: 2,750,000"));
	}

}

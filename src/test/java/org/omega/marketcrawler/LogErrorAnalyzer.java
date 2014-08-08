package org.omega.marketcrawler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LogErrorAnalyzer {

	private String logPath;
	
	public LogErrorAnalyzer(String logPath) {
		this.logPath = logPath;
	}
	
	@SuppressWarnings("unchecked")
	public void analyze() {
		try {
			InputStream is = new FileInputStream(logPath);
			List<String> lines = IOUtils.readLines(is);
			is.close();
			
//			Set<String> exceptions = new HashSet<>();
			TreeSet<String> exceptions = new TreeSet<>();
			Iterator<String> iter = lines.iterator();
			String line = null;
			String first = null, last = null;
			while (iter.hasNext() ) {
				line = iter.next();
				if (line.contains("fetch market data error") 
//						|| line.contains("Exception")
						) {
//					if (first == null)  first = line.substring(0, 16);
					exceptions.add(line.substring(0, 16));
				}
				iter.remove();
			}
			first = exceptions.first();
			last = exceptions.last();
			
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
			DateTime firstDt = formatter.parseDateTime(first);
			DateTime lastDt = formatter.parseDateTime(last);
			
			System.out.println("first :" + first);
			System.out.println("last  :" + last);
			
			long totalMinutes = (lastDt.getMillis() - firstDt.getMillis())/1000/60;
			
			System.out.println("totalMinutes: " + totalMinutes);
			System.out.println("exception/total is " + ((double)exceptions.size()/totalMinutes));
			
			
			System.out.println("exceptions.size(): " + exceptions.size());
			iter = exceptions.iterator();
			while ( iter.hasNext()) {
				line = iter.next();
//				if (line.contains("fetch market data error")) {
//					System.out.println(line);
//				}
				
				iter.remove();
			}
			System.out.println("exceptions.size(): " + exceptions.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		String logPath = "/programs/marketcrawler/jsw/marketcrawlerapp/app.log.2";
		LogErrorAnalyzer ana = new LogErrorAnalyzer(logPath);
		
		ana.analyze();
	}
	
}

package org.omega.marketcrawler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.omega.marketcrawler.common.Arith;
import org.omega.marketcrawler.common.Utils;

public class Misc2Test {

	public static void main(String[] args) throws ParseException {
		// 1404196332
		Date d = new Date(1404196332934l);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ss");
		System.out.println(sd.format(d));
		double ddd = 0.00000039;
		System.out.println(ddd);
		NumberFormat xx = new DecimalFormat("#.##");     
		System.out.println(xx.format(ddd));
		
		double x = 1.9841269841;
		System.out.println(String.format("%1$.1fy", x));
		System.out.println(String.format("%1$.2fy", x));
		
		String label = "BTC\\/USD";
		String[] ss = label.split("/");
		System.out.println(ss[0]);
		System.out.println(ss[1]);
		
		long x1 = 1405995720000l;
		long x2 = 1406082120000l;
		
		DateTime dt1 = new DateTime(DateTimeZone.UTC);
		dt1 = dt1.withMillis(x1);
		
		DateTime dt2 = new DateTime(DateTimeZone.UTC);
		dt2 = dt2.withMillis(x2);
		
		System.out.println(dt1);
		System.out.println(dt2);
		
		double dd1 = 0.00000001;
		double dd2 = 0.000000011;
		
		System.out.println(dd1 == dd2);
		
		long currMills = System.currentTimeMillis();
		
		Date gmt = Utils.convertDateZone(new Date(), Utils.TIME_ZONE_LOCAL, Utils.TIME_ZONE_GMT);
		DateTime currutc = new DateTime(gmt);
		System.out.println(currutc.getMillis() + ", " + currutc);
		System.out.println(currutc.minusDays(1).getMillis() + ", " + currutc.minusDays(1));
		System.out.println("----------UTC TIME-------------------------------------------------");
		
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Long.MAX_VALUE);
		System.out.println(System.currentTimeMillis());
		System.out.println(System.currentTimeMillis()/1000);
		
		/* 1406442064561
		 * 1406442064 */
		int secs = 1406251800;
		System.out.println();
		System.out.println(new Date((long) secs*1000));
		System.out.println(new DateTime().withMillis(0));
		System.out.println(new DateTime().withMillis(0).plusSeconds(secs));
		System.out.println(new DateTime((long) secs*1000));
		System.out.println("---------");

		String t1 =  "2014-07-28T02:41:43.773";
		String t2 =  "2014-07-28T02:41:43.77";
		
		String TIME_PATTERN_BITTREX = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN_BITTREX);
		
		long mills1 = sdf.parse(t1).getTime();
		long mills2 = sdf.parse(t2).getTime();
		System.out.println("mills1: " + mills1 + ", mills2: " + mills2);
		
		Date d1 = new Date(mills1);
		Date d2 = new Date(mills2);
		
		String tt1 = sdf.format(d1);
		String tt2 = sdf.format(d2);
		System.out.println("tt1: " + tt1);
		System.out.println("tt2: " + tt2);
		System.out.println("------------------------");
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(TIME_PATTERN_BITTREX);
		long mills3 = formatter.parseMillis(t1);
		long mills4 = formatter.parseMillis(t2);
		System.out.println("mills3: " + mills3 + ", mills4: " + mills4);
		
		Date d3 = new Date(mills3);
		Date d4 = new Date(mills4);
		String tt3 = formatter.print(mills3);
		String tt4 = formatter.print(mills4);
		System.out.println("tt3: " + tt3);
		System.out.println("tt4: " + tt4);
		System.out.println("------------------------");
		
		
		String price = "1.06844000";
		Float f = Float.valueOf(price);
		System.out.println(f);
		System.out.println(String.format("%.5f", f));
		System.out.println(String.format("%.8f", f));
		System.out.println(String.format("%.8f", Arith.multiply(f, 1)));
		System.out.println(String.format("%.8f", Arith.divide(f, 1, 8)));
		System.out.println(String.format("%.8f", Arith.multiply(f, 1.0)));
		System.out.println(String.format("%.8f", Arith.divide(f, 1.0, 8)));
		
		long time = (long) Arith.multiply(Double.valueOf("1406509156.05"), 1000);
		System.out.println(new DateTime(time, DateTimeZone.UTC));
		
	}
	
}

package org.omega.marketcrawler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Misc2Test {

	public static void main(String[] args) {
		// 1404196332
		Date d = new Date(1404196332934l);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ss");
		System.out.println(sd.format(d));
		double ddd = 0.00000039;
		System.out.println(ddd);
		NumberFormat formatter = new DecimalFormat("#.##");     
		System.out.println(formatter.format(ddd));
		
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
		System.out.println(currMills);
		System.out.println(new DateTime(currMills).minusDays(1).getMillis());
		System.out.println("-----------------------------------------------------------");
		
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
		
	}
	
}

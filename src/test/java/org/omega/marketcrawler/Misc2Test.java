package org.omega.marketcrawler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	}
	
}

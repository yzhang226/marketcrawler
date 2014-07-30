package org.omega.marketcrawler;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.omega.marketcrawler.operator.Bittrex;

public class DateTimeTest {

	public static void main(String[] args) {

		String time = "2014-07-29T03:14:02.147";
		DateTimeFormatter formatter = DateTimeFormat.forPattern(Bittrex.TIME_PATTERN_BITTREX);
		long mills = Bittrex.instance().parseMillsecs(time, formatter);
		System.out.println(mills);
		
		Date d = new Date(mills);
		System.out.println(d);
		
		DateTime dt = new DateTime(mills);
		System.out.println(dt);
		
		dt = new DateTime(mills, DateTimeZone.UTC);
		System.out.println(dt);
	}
	
}

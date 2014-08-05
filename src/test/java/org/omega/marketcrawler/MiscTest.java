package org.omega.marketcrawler;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.omega.marketcrawler.common.Arith;
import org.omega.marketcrawler.common.Utils;

public class MiscTest {

	public static void main(String[] args) throws Exception {
		// Create a ResultSetHandler implementation to convert the first row into an Object[].
//		ResultSetHandler<List<Object[]>> h = new ResultSetHandler<List<Object[]>>() {
//		    public List<Object[]> handle(ResultSet rs) throws SQLException {
//		        if (!rs.next()) {
//		            return null;
//		        }
//		    
//		        ResultSetMetaData meta = rs.getMetaData();
//		        int cols = meta.getColumnCount();
//		        
//		        List<Object[]> lis = new ArrayList<Object[]>();
//		        
//		        do {
//		        	Object[] result = new Object[cols];
//			        for (int i = 0; i < cols; i++) {
//			            result[i] = rs.getObject(i + 1);
//			        }
//			        lis.add(result);
//		        } while (rs.next());
//		        
//		        return lis;
//		    }
//		};
//
//		// Create a QueryRunner that will use connections from the given DataSource
//		QueryRunner run = new QueryRunner();
//		Connection conn = DbManager.inst().getConnection();
//		// Execute the query and get the results back from the handler
//		List<Object[]> objs = run.query(conn, "SELECT * FROM bct_topic", h);
//		
//		System.out.println("objs.size is " + objs.size());
//		
//		StringBuilder xx = new StringBuilder();
//		for (Object[] obj : objs) {
//			xx = new StringBuilder();
//			for (Object x : obj) {
//				xx.append(x).append(",");
//			}
//			
//			System.out.println(xx.toString());
//			
//		}
//		
//		conn.close();
		String field = "1406580318.0513";
		long nanoSecs = (long) Arith.multiply(Double.valueOf(field), 10000);
		System.out.println("total: " + nanoSecs);
		System.out.println("nano_time: " + ((byte) (nanoSecs%10)));
		System.out.println("tradetime: " + (nanoSecs/10));
		
		
		String fieldValue = "195.89393691";
		Float flo = Float.valueOf(fieldValue);
		System.out.println(flo);
		System.out.println(Utils.right8Pad(flo));
		
		Double dou = Double.valueOf(fieldValue);
		System.out.println(dou);
		System.out.println(Utils.right8Pad(dou));
		
		long futTime = (long) Integer.MAX_VALUE*1000;
		DateTime fut = new DateTime(futTime);
		System.out.println(fut);
		
		futTime = Long.MAX_VALUE;
		fut = new DateTime(futTime);
		System.out.println(fut);
		
//		re.setNanoTime((byte) (nanoSecs%10));
//		re.setTradeTime(nanoSecs/10);
		System.out.println("------------------------------------");
		long mintpalMax = 1407220133278l;
		long bittrexMax = 1407190592090l;
		
		DateTime mintdt = new DateTime(mintpalMax);
//		DateTime mintdt2 = new DateTime(mintpalMax, DateTimeZone.UTC);
		DateTime bittdt = new DateTime(bittrexMax);
//		DateTime bittdt2 = new DateTime(bittrexMax, DateTimeZone.UTC);
		System.out.println(mintdt);
//		System.out.println(mintdt2);
		System.out.println("******************");
		System.out.println(bittdt);
//		System.out.println(bittdt2);
		
		System.out.println("00000000000000000000000000000000000000000");
		
		String dtext = "2014-08-05T06:16:32.09";
		
		String bittrexPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(bittrexPattern)
				.withZoneUTC();
//		formatter = formatter.withZone(DateTimeZone.UTC);
		
		long bittmillis3 = formatter.parseMillis(dtext);
		System.out.println(new DateTime(bittmillis3));
		
		formatter = DateTimeFormat.forPattern(bittrexPattern);
//		formatter.withZone(DateTimeZone.UTC);
		
		long bittmillis4 = formatter.parseMillis(dtext);
		System.out.println(new DateTime(bittmillis4));
		
		for (String tz : TimeZone.getAvailableIDs() ) {
			if (tz.contains("New")) {
				System.out.println(tz);
			}
			if (tz.contains("UTC")) {
				System.out.println(tz);
			}
		}
		
		System.out.println(TimeZone.getTimeZone("America/New_York"));
		System.out.println(TimeZone.getTimeZone("UTC"));
	}
	
}

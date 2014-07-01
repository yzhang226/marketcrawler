package org.omega.marketcrawler.exchange;

import java.util.List;

import org.omega.marketcrawler.entity.TradeRecord;
import org.omega.marketcrawler.net.NetUtils;

public final class Bittrex extends TradeOperator {

	private static final Bittrex inst = new Bittrex();
	
	private Bittrex() {}
	
	public static Bittrex instance() {
		return inst;
	}
	
	// https://bittrex.com/api/v1/public/getmarkethistory?market=BTC-DOGE&count=5
	public String getHistoryJsonText(String watchedSymbol, String exchangeSymbol) {
		StringBuilder api = new StringBuilder("https://bittrex.com/api/v1/public/getmarkethistory?market=");
		api.append(exchangeSymbol).append("-").append(watchedSymbol);
		
		return NetUtils.accessDirectly(api.toString());
	}
	
	/*
	 * {"success":true,"message":"","result":[
	 * {"Id":290104,"TimeStamp":"2014-07-01T05:21:59.99","Quantity":1000.00000000,"Price":0.00000039,"Total":0.00039000,"FillType":"PARTIAL_FILL","OrderType":"SELL"},
	 * {"Id":290103,"TimeStamp":"2014-07-01T05:17:59.843","Quantity":104.00000000,"Price":0.00000039,"Total":0.00004056,"FillType":"PARTIAL_FILL","OrderType":"SELL"},
	 * {"Id":290100,"TimeStamp":"2014-07-01T05:06:26.463","Quantity":869.73512715,"Price":0.00000039,"Total":0.00033919,"FillType":"PARTIAL_FILL","OrderType":"BUY"},
	 * {"Id":290098,"TimeStamp":"2014-07-01T05:06:25.627","Quantity":341148.22824777,"Price":0.00000039,"Total":0.13304780,"FillType":"FILL","OrderType":"BUY"},
	 * {"Id":290097,"TimeStamp":"2014-07-01T05:06:25.623","Quantity":5856.66918812,"Price":0.00000039,"Total":0.00228410,"FillType":"PARTIAL_FILL","OrderType":"BUY"}]}
	 * (non-Javadoc)
	 * @see org.omega.marketcrawler.exchange.TradeOperator#getHistory(java.lang.String, java.lang.String)
	 */
	public List<TradeRecord> getHistory(String watchedSymbol, String exchangeSymbol) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}

package org.omega.marketcrawler.exchange;

import java.util.List;

import org.omega.marketcrawler.entity.MarketSummary;
import org.omega.marketcrawler.entity.MarketTrade;


public abstract class TradeOperator {

//	private int id;
	
	/*
	 * https://bittrex.com/Market/Index?MarketName=BTC-GUE
	 * https://www.mintpal.com/market/CINNI/BTC
	 * 
	 * market trades 
	 * https://api.mintpal.com/v2/market/trades/{COIN}/{EXCHANGE}
	 * https://api.mintpal.com/v2/market/trades/MINT/BTC
	 * 
	 * https://bittrex.com/api/v1/public/getmarkethistory?market=BTC-DOGE&count=5
	 * 
	 */
//	public abstract String getHistoryAPI();
	
	/**
	 * 
	 * @param watchedSymbol - for example: MINT, DOGE
	 * @param exchangeSymbol - for example: BTC
	 * @return
	 */
//	public abstract String getHistoryJsonText(String watchedSymbol, String exchangeSymbol);
	
	public abstract List<MarketTrade> getMarketTrades(String watchedSymbol, String exchangeSymbol);
	
	public abstract List<MarketSummary> getMarketSummaries();
	

}

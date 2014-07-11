package org.omega.marketcrawler.operator;

import java.util.List;

import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;

public final class OperatorFactory {

	private OperatorFactory() {}
	
	public static TradeOperator get(String operatorName) {
		switch (operatorName) {
		case Mintpal.NAME:
			return Mintpal.instance();
		case Bittrex.NAME:
			return Bittrex.instance();
		case Poloniex.NAME:
			return Poloniex.instance();
		case Cryptsy.NAME:
			return Cryptsy.instance();
		default:
			return null;
		}
	}
	
	public static List<MarketTrade> getMarketTrades(WatchListItem item) {
		return get(item.getOperator()).getMarketTrades(item);
	}
	
}

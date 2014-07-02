package org.omega.marketcrawler.exchange;

public final class OperatorFactory {

	private OperatorFactory() {}
	
	public static TradeOperator get(String operatorName) {
		switch (operatorName) {
		case Mintpal.NAME:
			return Mintpal.instance();
		case Bittrex.NAME:
			return Bittrex.instance();
		default:
			return null;
		}
	}
	
}

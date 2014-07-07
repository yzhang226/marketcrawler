package org.omega.marketcrawler.entity;

public class WatchListItem extends _BaseEntity {

	private static final long serialVersionUID = -5398296385071762593L;

	private String operator;// mintpal, bittrex
	private String watchedSymbol;// CINNI, MINT
	private String exchangeSymbol;// BTC
	
	public WatchListItem() {}
	
	public WatchListItem(String operator, String watchedSymbol, String exchangeSymbol) {
		this.operator = operator;
		this.watchedSymbol = watchedSymbol;
		this.exchangeSymbol = exchangeSymbol;
	}
	
	public String getMarketTradeTable() {
		return new StringBuilder("trade_")
		.append(operator.toLowerCase()).append("_")
		.append(exchangeSymbol.toLowerCase()).append("_")
		.append(watchedSymbol.toLowerCase()).toString();
	}
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getWatchedSymbol() {
		return watchedSymbol;
	}
	public void setWatchedSymbol(String watchedSymbol) {
		this.watchedSymbol = watchedSymbol;
	}
	public String getExchangeSymbol() {
		return exchangeSymbol;
	}
	public void setExchangeSymbol(String exchangeSymbol) {
		this.exchangeSymbol = exchangeSymbol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((exchangeSymbol == null) ? 0 : exchangeSymbol.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result
				+ ((watchedSymbol == null) ? 0 : watchedSymbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WatchListItem other = (WatchListItem) obj;
		if (exchangeSymbol == null) {
			if (other.exchangeSymbol != null)
				return false;
		} else if (!exchangeSymbol.equals(other.exchangeSymbol))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (watchedSymbol == null) {
			if (other.watchedSymbol != null)
				return false;
		} else if (!watchedSymbol.equals(other.watchedSymbol))
			return false;
		return true;
	}
	
	public String toReadableText() {
		StringBuilder sb = new StringBuilder();
		sb.append(operator).append(",")
		.append(watchedSymbol).append(",")
		.append(exchangeSymbol).append(",");
		return sb.toString();
	}
	
	public String toSimpleText() {
		StringBuilder sb = new StringBuilder();
		sb.append(operator).append("_")
		.append(watchedSymbol).append("_")
		.append(exchangeSymbol).append("_");
		return sb.toString();
	}
	
	
	
}

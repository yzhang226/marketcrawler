package org.omega.marketcrawler.entity;

public class WatchListItem extends _BaseEntity {

	private static final long serialVersionUID = -5398296385071762593L;
	
	private int id;
	private byte status;
	private String operator;// mintpal, bittrex
	private String watchedSymbol;// CINNI, MINT
	private String exchangeSymbol;// BTC
	private Integer marketId;
	
	public WatchListItem() {}
	
	public WatchListItem(String operator, String watchedSymbol, String exchangeSymbol) {
		this.operator = operator;
		setWatchedSymbol(watchedSymbol);
		setExchangeSymbol(exchangeSymbol);
	}
	
	private String market_trade_table;
	public String toMarketTradeTable() {
		if (market_trade_table == null) {
			StringBuilder table = new StringBuilder("trade_");
			table.append(operator.toLowerCase()).append("_");
			table.append(exchangeSymbol.toLowerCase()).append("_");
			table.append(watchedSymbol.toLowerCase());
			
			market_trade_table = table.toString();
		}
		return market_trade_table;
	}
	
	// getter, setter
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
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
		this.watchedSymbol = watchedSymbol != null ? watchedSymbol.toUpperCase() : watchedSymbol;
	}
	public String getExchangeSymbol() {
		return exchangeSymbol;
	}
	public void setExchangeSymbol(String exchangeSymbol) {
		this.exchangeSymbol =  exchangeSymbol != null ? exchangeSymbol.toUpperCase() : exchangeSymbol;
	}
	public Integer getMarketId() {
		return marketId;
	}
	public void setMarketId(Integer marketId) {
		this.marketId = marketId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((marketId == null) ? 0 : marketId.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + status;
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
		if (marketId == null) {
			if (other.marketId != null)
				return false;
		} else if (!marketId.equals(other.marketId))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (status != other.status)
			return false;
		if (watchedSymbol == null) {
			if (other.watchedSymbol != null)
				return false;
		} else if (!watchedSymbol.equals(other.watchedSymbol))
			return false;
		return true;
	}

	private String readable_text;
	public String toReadableText() {
		if (readable_text == null) {
			StringBuilder sb = new StringBuilder(toMarketTradeTable());
			if (marketId != null) sb.append("_").append(marketId);
			readable_text = sb.toString();
		}
		
		return readable_text;
	}
	
}

package org.omega.marketcrawler.entity;

public class MarketTrade extends _BaseEntity {

	private static final long serialVersionUID = -5408828461710055307L;
	
	public static final String TYPE_TEXT_SELL = "sell";
	public static final String TYPE_TEXT_BUY = "buy";
	
	public static final byte TRADE_TYPE_NA = 0;
	public static final byte TRADE_TYPE_BUY = 1;
	public static final byte TRADE_TYPE_SELL = 2;

	private long tradeTime;
	/**  NA - 0, buy - 1, sell - 2 */
	private byte tradeType;
	private double price;
	private double totalUnits;
	private double totalCost;
	public static byte parseTradeType(String textType) {
		if (textType == null || textType.trim().length() == 0) 
			return TRADE_TYPE_NA ;
		if (TYPE_TEXT_BUY.equalsIgnoreCase(textType)) {
			return TRADE_TYPE_BUY;
		} else if (TYPE_TEXT_SELL.equalsIgnoreCase(textType)) {
			return TRADE_TYPE_SELL;
		}
		return TRADE_TYPE_NA;
	}
	//
	public long getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(long tradeTime) {
		this.tradeTime = tradeTime;
	}
	public byte getTradeType() {
		return tradeType;
	}
	public void setTradeType(byte tradeType) {
		this.tradeType = tradeType;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getTotalUnits() {
		return totalUnits;
	}
	public void setTotalUnits(double totalUnits) {
		this.totalUnits = totalUnits;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	
	public String toReadableText() {
		StringBuilder sb = new StringBuilder();
		sb.append(tradeTime).append(",")
		.append(tradeType).append(",")
		.append(price).append(",")
		.append(totalUnits).append(",")
		.append(totalCost).append(",");
		return sb.toString();
	}
	
	public String toInsertValue() {
		StringBuilder sb = new StringBuilder();
		sb.append(tradeTime).append(",")
		.append(tradeType).append(",")
		.append(String.valueOf(price)).append(",")
		.append(String.valueOf(totalUnits)).append(",")
		.append(String.valueOf(totalCost)).append(",");
		return sb.toString();
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (tradeTime ^ (tradeTime >>> 32));
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarketTrade other = (MarketTrade) obj;
		if (tradeTime != other.tradeTime)
			return false;
		return true;
	}
	
}

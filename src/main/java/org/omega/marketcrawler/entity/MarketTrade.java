package org.omega.marketcrawler.entity;

public class MarketTrade extends _BaseEntity {

	
	private static final long serialVersionUID = -5408828461710055307L;
	
	public static final byte TRADE_TYPE_NA = 0;
	public static final byte TRADE_TYPE_BUY = 1;
	public static final byte TRADE_TYPE_SELL = 2;

	private long tradeTime;
	/**
	 * NA - 0, buy - 1, sell - 2
	 */
	private byte tradeType;
	private double price;
	private double totalUnits;
	private double totalCost;
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
	
}

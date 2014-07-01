package org.omega.marketcrawler.entity;

public class TradeRecord extends _BaseEntity {

	private static final long serialVersionUID = -1558808898415695311L;
	
	public static final short TYPE_SELL = new Short("0").shortValue();
	public static final short TYPE_BUY = new Short("1").shortValue();
	
	
	private long tradeTime;
	/**
	 * sell - 0
	 * buy - 1
	 */
	private short tradeType;
	private double price;
	private double totalUnits;
	private double totalCost;
	public long getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(long tradeTime) {
		this.tradeTime = tradeTime;
	}
	public short getTradeType() {
		return tradeType;
	}
	public void setTradeType(short tradeType) {
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

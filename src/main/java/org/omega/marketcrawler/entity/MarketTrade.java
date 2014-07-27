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
	private float price;
	private float totalUnits;
	private float totalCost;
	/** in bittrex, cryptsy, the tradeTime and tradeType will not identify unique one  */
	private Integer tradeId;
	
	
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
	
	public String toReadableText() {
		StringBuilder sb = new StringBuilder();
		sb.append(tradeTime).append(",").append(tradeType).append(",")
		  .append(price).append(",").append(totalUnits).append(",")
		  .append(totalCost);
		return sb.toString();
	}

	public MarketTrade copy() {
		MarketTrade copy = new MarketTrade();
		
		copy.setTradeTime(tradeTime);
		copy.setTradeType(tradeType);
		copy.setPrice(price);
		copy.setTotalUnits(totalUnits);
		copy.setTotalCost(totalCost);
		
		return copy;
	}
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
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getTotalUnits() {
		return totalUnits;
	}
	public void setTotalUnits(float totalUnits) {
		this.totalUnits = totalUnits;
	}
	public float getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}
	public Integer getTradeId() {
		return tradeId;
	}
	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}
	
}

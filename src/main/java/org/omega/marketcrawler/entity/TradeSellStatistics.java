package org.omega.marketcrawler.entity;

public class TradeSellStatistics extends _BaseEntity {

	private static final long serialVersionUID = 7832094534651751485L;
	
	private int itemId;
	private long startTime;
	private long endTime;
	
	private double sellWatchedVol;
	private double sellExchangeVol;
	private double avgSellPrice;
	private int sellCount;
	
	public TradeSellStatistics() { }

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public double getSellWatchedVol() {
		return sellWatchedVol;
	}

	public void setSellWatchedVol(double sellWatchedVol) {
		this.sellWatchedVol = sellWatchedVol;
	}

	public double getSellExchangeVol() {
		return sellExchangeVol;
	}

	public void setSellExchangeVol(double sellExchangeVol) {
		this.sellExchangeVol = sellExchangeVol;
	}

	public double getAvgSellPrice() {
		return avgSellPrice;
	}

	public void setAvgSellPrice(double avgSellPrice) {
		this.avgSellPrice = avgSellPrice;
	}

	public int getSellCount() {
		return sellCount;
	}

	public void setSellCount(int sellCount) {
		this.sellCount = sellCount;
	}
	
}

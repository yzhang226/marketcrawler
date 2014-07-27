package org.omega.marketcrawler.entity;

public class TradeBuyStatistics extends _BaseEntity {

	private static final long serialVersionUID = -8654747362183067479L;
	
	private int itemId;
	private long startTime;
	private long endTime;
	
	private double buyWatchedVol;
	private double buyExchangeVol;
	private double avgBuyPrice;
	private int buyCount;
	
	public TradeBuyStatistics() { }

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

	public double getBuyWatchedVol() {
		return buyWatchedVol;
	}

	public void setBuyWatchedVol(double buyWatchedVol) {
		this.buyWatchedVol = buyWatchedVol;
	}

	public double getBuyExchangeVol() {
		return buyExchangeVol;
	}

	public void setBuyExchangeVol(double buyExchangeVol) {
		this.buyExchangeVol = buyExchangeVol;
	}

	public double getAvgBuyPrice() {
		return avgBuyPrice;
	}

	public void setAvgBuyPrice(double avgBuyPrice) {
		this.avgBuyPrice = avgBuyPrice;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}
	
}

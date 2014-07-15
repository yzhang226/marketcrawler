package org.omega.marketcrawler.entity;

public class TradeStatistics extends _BaseEntity {

	private static final long serialVersionUID = -8009878791709947226L;
	
	private int itemId;
	private long startTime;
	private long endTime;
	private double open;
	private double high;
	private double low;
	private double close;
	private double watchedVol;
	private double exchangeVol;
	private int count;
	
	public TradeStatistics() { }

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

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getWatchedVol() {
		return watchedVol;
	}

	public void setWatchedVol(double watchedVol) {
		this.watchedVol = watchedVol;
	}

	public double getExchangeVol() {
		return exchangeVol;
	}

	public void setExchangeVol(double exchangeVol) {
		this.exchangeVol = exchangeVol;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}

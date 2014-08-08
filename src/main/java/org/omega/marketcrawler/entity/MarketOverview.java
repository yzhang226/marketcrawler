package org.omega.marketcrawler.entity;

public class MarketOverview extends _BaseEntity {

	private static final long serialVersionUID = -8464139867409065752L;
	
	private short itemId;
	private int startTime;
	private int endTime;
	private float open;
	private float high;
	private float low;
	private float close;
	private float watchedVol;
	private float exchangeVol;
	
	public short getItemId() {
		return itemId;
	}
	public void setItemId(short itemId) {
		this.itemId = itemId;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public float getWatchedVol() {
		return watchedVol;
	}
	public void setWatchedVol(float watchedVol) {
		this.watchedVol = watchedVol;
	}
	public float getExchangeVol() {
		return exchangeVol;
	}
	public void setExchangeVol(float exchangeVol) {
		this.exchangeVol = exchangeVol;
	}
	
}

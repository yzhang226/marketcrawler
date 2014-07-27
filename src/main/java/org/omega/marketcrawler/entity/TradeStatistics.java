package org.omega.marketcrawler.entity;

public class TradeStatistics extends _BaseEntity {

	private static final long serialVersionUID = -5043870448507147422L;
	
	private short itemId;
	private int startTime;
	private int endTime;
	private float open;
	private float high;
	private float low;
	private float close;
	private float watchedVol;
	private float exchangeVol;
	private short count;
	
	private Float buyWatchedVol;
	private Float buyExchangeVol;
	private Float buyAvgPrice;
	private Short buyCount;
	
	private Float sellWatchedVol;
	private Float sellExchangeVol;
	private Float sellAvgPrice;
	private Short sellCount;
	
	public TradeStatistics() { }

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

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public Float getBuyWatchedVol() {
		return buyWatchedVol;
	}

	public void setBuyWatchedVol(Float buyWatchedVol) {
		this.buyWatchedVol = buyWatchedVol;
	}

	public Float getBuyExchangeVol() {
		return buyExchangeVol;
	}

	public void setBuyExchangeVol(Float buyExchangeVol) {
		this.buyExchangeVol = buyExchangeVol;
	}

	public Float getBuyAvgPrice() {
		return buyAvgPrice;
	}

	public void setBuyAvgPrice(Float buyAvgPrice) {
		this.buyAvgPrice = buyAvgPrice;
	}

	public Short getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Short buyCount) {
		this.buyCount = buyCount;
	}

	public Float getSellWatchedVol() {
		return sellWatchedVol;
	}

	public void setSellWatchedVol(Float sellWatchedVol) {
		this.sellWatchedVol = sellWatchedVol;
	}

	public Float getSellExchangeVol() {
		return sellExchangeVol;
	}

	public void setSellExchangeVol(Float sellExchangeVol) {
		this.sellExchangeVol = sellExchangeVol;
	}

	public Float getSellAvgPrice() {
		return sellAvgPrice;
	}

	public void setSellAvgPrice(Float sellAvgPrice) {
		this.sellAvgPrice = sellAvgPrice;
	}

	public Short getSellCount() {
		return sellCount;
	}

	public void setSellCount(Short sellCount) {
		this.sellCount = sellCount;
	}
}

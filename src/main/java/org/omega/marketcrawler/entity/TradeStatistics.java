package org.omega.marketcrawler.entity;

public class TradeStatistics extends _BaseEntity {

	private static final long serialVersionUID = -5043870448507147422L;
	
	private short itemId;
	private int startTime;
	private int endTime;
	private double open;
	private double high;
	private double low;
	private double close;
	private double watchedVol;
	private double exchangeVol;
	private short count;
	
	private Double buyWatchedVol;
	private Double buyExchangeVol;
	private Double buyAvgPrice;
	private Short buyCount;
	
	private Double sellWatchedVol;
	private Double sellExchangeVol;
	private Double sellAvgPrice;
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

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public Double getBuyWatchedVol() {
		return buyWatchedVol;
	}

	public void setBuyWatchedVol(Double buyWatchedVol) {
		this.buyWatchedVol = buyWatchedVol;
	}

	public Double getBuyExchangeVol() {
		return buyExchangeVol;
	}

	public void setBuyExchangeVol(Double buyExchangeVol) {
		this.buyExchangeVol = buyExchangeVol;
	}

	public Double getBuyAvgPrice() {
		return buyAvgPrice;
	}

	public void setBuyAvgPrice(Double buyAvgPrice) {
		this.buyAvgPrice = buyAvgPrice;
	}

	public Short getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Short buyCount) {
		this.buyCount = buyCount;
	}

	public Double getSellWatchedVol() {
		return sellWatchedVol;
	}

	public void setSellWatchedVol(Double sellWatchedVol) {
		this.sellWatchedVol = sellWatchedVol;
	}

	public Double getSellExchangeVol() {
		return sellExchangeVol;
	}

	public void setSellExchangeVol(Double sellExchangeVol) {
		this.sellExchangeVol = sellExchangeVol;
	}

	public Double getSellAvgPrice() {
		return sellAvgPrice;
	}

	public void setSellAvgPrice(Double sellAvgPrice) {
		this.sellAvgPrice = sellAvgPrice;
	}

	public Short getSellCount() {
		return sellCount;
	}

	public void setSellCount(Short sellCount) {
		this.sellCount = sellCount;
	}
}

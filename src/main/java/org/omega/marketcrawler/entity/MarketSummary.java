package org.omega.marketcrawler.entity;

import java.util.Map;

public class MarketSummary extends _BaseEntity {

	private static final long serialVersionUID = -7201154621168472099L;
	
	private int marketId;
	private String coinName;
	private String watchedSymbol;
	private String exchangeSymbol;
	private double lastPrice;
	private double yesterdayPrice;
	private Double change;
	private double highest24h;
	private double lowest24h;
	private double volume24h;
	private double topBid;
	private double topAsk;
	
	// 
	public int getMarketId() {
		return marketId;
	}
	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public String getWatchedSymbol() {
		return watchedSymbol;
	}
	public void setWatchedSymbol(String watchedSymbol) {
		this.watchedSymbol = watchedSymbol;
	}
	public String getExchangeSymbol() {
		return exchangeSymbol;
	}
	public void setExchangeSymbol(String exchangeSymbol) {
		this.exchangeSymbol = exchangeSymbol;
	}
	public double getLastPrice() {
		return lastPrice;
	}
	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}
	public double getYesterdayPrice() {
		return yesterdayPrice;
	}
	public void setYesterdayPrice(double yesterdayPrice) {
		this.yesterdayPrice = yesterdayPrice;
	}
	public Double getChange() {
		return change;
	}
	public void setChange(Double change) {
		this.change = change;
	}
	public double getHighest24h() {
		return highest24h;
	}
	public void setHighest24h(double highest24h) {
		this.highest24h = highest24h;
	}
	public double getLowest24h() {
		return lowest24h;
	}
	public void setLowest24h(double lowest24h) {
		this.lowest24h = lowest24h;
	}
	public double getVolume24h() {
		return volume24h;
	}
	public void setVolume24h(double volume24h) {
		this.volume24h = volume24h;
	}
	public double getTopBid() {
		return topBid;
	}
	public void setTopBid(double topBid) {
		this.topBid = topBid;
	}
	public double getTopAsk() {
		return topAsk;
	}
	public void setTopAsk(double topAsk) {
		this.topAsk = topAsk;
	}
	
	public String toReadableText() {
		StringBuilder sb = new StringBuilder();
		sb.append(marketId).append(", ")
		  .append(coinName).append(", ")
		  .append(watchedSymbol).append(", ")
		  .append(exchangeSymbol).append(", ")
		  .append(lastPrice).append(", ")
		  .append(yesterdayPrice).append(", ")
		  .append(change).append(", ")
		  .append(highest24h).append(", ")
		  .append(lowest24h).append(", ")
		  .append(volume24h).append(", ")
		  .append(topBid).append(", ")
		  .append(topAsk).append(", ");
		return sb.toString();
	}

}

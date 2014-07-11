package org.omega.marketcrawler.entity;

import java.sql.Timestamp;


public class MarketSummary extends _BaseEntity {

	private static final long serialVersionUID = -7201154621168472099L;
	
	private String operator;
	private String watchedSymbol;
	private String exchangeSymbol;
	private Integer marketId;
	private String watchedCoinName;
	private String exchangeCoinName;
	private double lastPrice;
	private double yesterdayPrice;
	private Double fluctuation;// chnage
	private double highest24h;
	private double lowest24h;
	private double volume24h;
	private double coinVolume24h;
	private double topBid;
	private double topAsk;
	private Timestamp updateTime;
	
	
	// 
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
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
	public Integer getMarketId() {
		return marketId;
	}
	public void setMarketId(Integer marketId) {
		this.marketId = marketId;
	}
	public String getWatchedCoinName() {
		return watchedCoinName;
	}
	public void setWatchedCoinName(String watchedCoinName) {
		this.watchedCoinName = watchedCoinName;
	}
	public String getExchangeCoinName() {
		return exchangeCoinName;
	}
	public void setExchangeCoinName(String exchangeCoinName) {
		this.exchangeCoinName = exchangeCoinName;
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
	public Double getFluctuation() {
		return fluctuation;
	}
	public void setFluctuation(Double fluctuation) {
		this.fluctuation = fluctuation;
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
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public double getCoinVolume24h() {
		return coinVolume24h;
	}
	public void setCoinVolume24h(double coinVolume24h) {
		this.coinVolume24h = coinVolume24h;
	}
	
	public String toReadableText() {
		StringBuilder sb = new StringBuilder();
		sb.append(marketId).append(", ")
		  .append(operator).append(", ")
		  .append(watchedSymbol).append(", ")
		  .append(exchangeSymbol).append(", ")
		  .append(watchedCoinName).append(", ")
		  .append(lastPrice).append(", ")
		  .append(yesterdayPrice).append(", ")
		  .append(fluctuation).append(", ")
		  .append(highest24h).append(", ")
		  .append(lowest24h).append(", ")
		  .append(volume24h).append(", ")
		  .append(topBid).append(", ")
		  .append(topAsk).append(", ")
		  .append(updateTime).append(", ");
		return sb.toString();
	}

}

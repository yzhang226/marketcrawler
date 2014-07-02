package org.omega.marketcrawler.common;


public final class SqlUtils {

	
	public static String preparedSql4History(String watchedSymbol, String exchangeSymbol, String operatorName) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(getHistoryTableName(watchedSymbol, exchangeSymbol, operatorName)).append(" (")
		  .append("trade_time, trade_type, price, total_units, total_cost").append(") VALUES (?, ?, ?, ?, ?)")
		  .append(" ON DUPLICATE KEY UPDATE total_cost=total_cost");
		return sb.toString();
	}
	
	public static String getCreateSql4MarketHistory(String watchedSymbol, String exchangeSymbol, String operatorName) {
		StringBuilder create = new StringBuilder();
		create.append("CREATE TABLE ").append(getHistoryTableName(watchedSymbol, exchangeSymbol, operatorName)).append(" ( ").append("\n")
		      .append("trade_time BIGINT NOT NULL , ").append("\n")
		      .append("trade_type TINYINT NULL , ").append("\n")
		      .append("price DOUBLE NULL ,").append("\n")
		      .append("total_units DOUBLE NULL ,").append("\n")
		      .append("total_cost DOUBLE NULL ,").append("\n")
		      .append("PRIMARY KEY (trade_time) );");
		
		return create.toString();
	}
	
	public static String getHistoryTableName(String watchedSymbol, String exchangeSymbol, String operatorName) {
		return new StringBuilder("history_")
		.append(operatorName.toLowerCase()).append("_")
		.append(exchangeSymbol.toLowerCase()).append("_")
		.append(watchedSymbol.toLowerCase()).toString();
	}
	
	
}

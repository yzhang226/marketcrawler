package org.omega.marketcrawler.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.SqlUtils;
import org.omega.marketcrawler.db.DbManager;
import org.omega.marketcrawler.db.MyDbUtils;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.exchange.OperatorFactory;


public class HistoryDataFetcher extends Thread {
	
	private static final Log log = LogFactory.getLog(HistoryDataFetcher.class);
	
	private String watchedSymbol;
	private String exchangeSymbol;
	private String operator;
	
	public HistoryDataFetcher(String watchedSymbol, String exchangeSymbol, String operator) {
		this.watchedSymbol = watchedSymbol;
		this.exchangeSymbol = exchangeSymbol;
		this.operator = operator;
	}
	
	public void run() {
		
		long start = System.currentTimeMillis();
		log.info("start HistoryDataFetcher");
		
		Connection conn = null;
		PreparedStatement prep = null;
		try {
			conn = DbManager.getConnection();
			
			if (!MyDbUtils.existTable(conn, SqlUtils.getHistoryTableName(watchedSymbol, exchangeSymbol, operator))) {
//				SqlUtils.getCreateSql4MarketHistory(watchedSymbol, exchangeSymbol, operator);
				conn.createStatement().execute(SqlUtils.getCreateSql4MarketHistory(watchedSymbol, exchangeSymbol, operator));
			}
			
			List<MarketTrade> records = OperatorFactory.get(operator).getMarketTrades(watchedSymbol, exchangeSymbol);
			
			
			prep = conn.prepareStatement(SqlUtils.preparedSql4History(watchedSymbol, exchangeSymbol, operator));
			
			for (MarketTrade re : records) {
				// trade_time, trade_type, price, total_units, total_cost
				prep.setLong(1, re.getTradeTime());
				prep.setShort(2, re.getTradeType());
				prep.setDouble(3, re.getPrice());
				prep.setDouble(4, re.getTotalUnits());
				prep.setDouble(5, re.getTotalCost());
				prep.addBatch();
			}
			
			prep.executeBatch();
			
			conn.commit();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				DbUtils.close(prep);
			} catch (SQLException e) {
				log.error("", e);
			}
		}
		
		long end = System.currentTimeMillis();
		log.info("end HistoryDataFetcher, total spent time is [" + (end -start) + "].");

	}

}

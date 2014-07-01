package org.omega.marketcrawler.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.SqlUtils;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.exchange.Mintpal;

public final class MyDbUtils {
	
	private static final Log log = LogFactory.getLog(MyDbUtils.class);
	
	public static boolean existTable(Connection conn, String tableName) {
		boolean exist = false;
		ResultSet rs = null;
		try {
			DatabaseMetaData meta = conn.getMetaData();
			rs = meta.getTables(null, null, tableName, new String[] { "TABLE" });
			while (rs.next()) {
				exist = true;
				break;
			}
		} catch (Exception e) {
			log.error("check exist table[" + tableName + "] error.", e);
		} finally {
			try {
				DbUtils.close(rs);
			} catch (SQLException e) {
				log.error("close result error.", e);
			}
		}
		
		return exist;
	}
	
	public static void main(String[] args) {
		String watchedSymbol = "BC";
		String exchangeSymbol = Symbol.BTC.name();
		System.out.println(existTable(DbManager.getConnection(), SqlUtils.getHistoryTableName(watchedSymbol, exchangeSymbol, Mintpal.NAME)));
		System.out.println(existTable(DbManager.getConnection(), "alt_coin"));
	}
	
	
	
	

}

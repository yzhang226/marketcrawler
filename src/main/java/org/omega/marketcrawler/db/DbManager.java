package org.omega.marketcrawler.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class DbManager {
	
	private static final ComboPooledDataSource pds = new ComboPooledDataSource();
	private DbManager() {}
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = pds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
}

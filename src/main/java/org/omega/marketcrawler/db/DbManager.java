package org.omega.marketcrawler.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class DbManager {
	
	private static final Log log = LogFactory.getLog(DbManager.class);
	
	private static final ComboPooledDataSource pds = new ComboPooledDataSource();
	
	private static final DbManager manager = new DbManager();
	private static final QueryRunner runner = new QueryRunner();
	
	
	private DbManager() {}
	
	public static DbManager inst() {
		return manager;
	}
	
	public Connection getConnection() throws SQLException {
		Connection conn = pds.getConnection();
		conn.setAutoCommit(true);
		return conn;
	}
	
	public boolean existTable(String tableName) throws SQLException {
		boolean exist = false;
		DatabaseMetaData meta = getConnection().getMetaData();
		ResultSet rs = meta.getTables(null, null, tableName, new String[] { "TABLE" });
		if (rs.next()) { exist = true; }

		DbUtils.close(rs);

		return exist;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object query(String sql, ResultSetHandler handler) throws SQLException {
		return runner.query(getConnection(), sql, handler);
	}
	
	public int[] batch(String sql, Object[][] params) throws SQLException {
		return runner.batch(getConnection(), sql.toString(), params);
	}
	
	public int execute(String sql) throws SQLException {
		return runner.update(getConnection(), sql);
	}
	
	
	
	
}

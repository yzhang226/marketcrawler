package org.omega.marketcrawler.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class DbManager {
	
	private static final Log log = LogFactory.getLog(DbManager.class);
	
	private static final DbManager manager = new DbManager();
	private static final QueryRunner runner = new QueryRunner();
	
	private DbManager() {}
	
	public static DbManager inst() {
		return manager;
	}
	
	public Connection getConnection() throws SQLException {
		return getConnection(true);
	}
	public Connection getConnection(boolean autoCommit) throws SQLException {
		Connection conn = PoolUtils.inst().getConnection();
		conn.setAutoCommit(autoCommit);
		return conn;
	}
	
	public boolean existTable(String tableName) throws SQLException {
		boolean exist = false;
		Connection conn = getConnection();
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet rs = meta.getTables(null, null, tableName, new String[] { "TABLE" });
		if (rs.next()) { exist = true; }

		DbUtils.close(rs);
		DbUtils.close(conn);

		return exist;
	}
	
	public <T> T query(String sql, ResultSetHandler<T> handler, Object... params) throws SQLException {
		Connection conn = getConnection();
		T resu = runner.query(conn, sql, handler, params);
		DbUtils.close(conn);
		return resu;
	}
	
	public Object[] queryUnique(String sql, Object... params) throws SQLException {
		ResultSetHandler<Object[]> handler = new ArrayHandler();
		Object[] resu = DbManager.inst().query(sql, handler, params);
		return resu;
	}
	
	public int[] batch(String sql, Object[][] params) throws SQLException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
        int[] rows = null;
        try {
        	conn = getConnection(false);
    		
            stmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                fillStatement(stmt, params[i]);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();
            
            conn.commit();
        } catch (SQLException e) {
           throw e;
        } finally {
        	try {
        		DbUtils.close(stmt);
			} catch (Exception e2) {
				log.error("close PreparedStatement error.", e2);
			}
        	try {
        		DbUtils.close(conn);
			} catch (Exception e2) {
				log.error("close connection error.", e2);
			}
        }
		
		return rows;
	}
	
	private void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				stmt.setObject(i + 1, params[i]);
			} else {
				int sqlType = Types.VARCHAR;
				stmt.setNull(i + 1, sqlType);
			}
		}
	}

	public int execute(String sql, Object... params) throws SQLException {
		Connection conn = getConnection();
		int resu = runner.update(conn, sql, params);
		DbUtils.close(conn);
		return resu;
	}
	
	
	
}

package org.omega.marketcrawler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.omega.marketcrawler.db.DbManager;

public class MiscTest {

	public static void main(String[] args) throws Exception {
		// Create a ResultSetHandler implementation to convert the first row into an Object[].
		ResultSetHandler<List<Object[]>> h = new ResultSetHandler<List<Object[]>>() {
		    public List<Object[]> handle(ResultSet rs) throws SQLException {
		        if (!rs.next()) {
		            return null;
		        }
		    
		        ResultSetMetaData meta = rs.getMetaData();
		        int cols = meta.getColumnCount();
		        
		        List<Object[]> lis = new ArrayList<Object[]>();
		        
		        do {
		        	Object[] result = new Object[cols];
			        for (int i = 0; i < cols; i++) {
			            result[i] = rs.getObject(i + 1);
			        }
			        lis.add(result);
		        } while (rs.next());
		        
		        return lis;
		    }
		};

		// Create a QueryRunner that will use connections from the given DataSource
		QueryRunner run = new QueryRunner();
		Connection conn = DbManager.inst().getConnection();
		// Execute the query and get the results back from the handler
		List<Object[]> objs = run.query(conn, "SELECT * FROM bct_topic", h);
		
		System.out.println("objs.size is " + objs.size());
		
		StringBuilder xx = new StringBuilder();
		for (Object[] obj : objs) {
			xx = new StringBuilder();
			for (Object x : obj) {
				xx.append(x).append(",");
			}
			
			System.out.println(xx.toString());
			
		}
		
		conn.close();
		
		
	}
	
}

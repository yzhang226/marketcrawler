package org.omega.marketcrawler.db;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AltCoinService {

	private static final Log log = LogFactory.getLog(AltCoinService.class);
	
	public static final byte STATUS_ACTIVE = 0;
	public static final byte STATUS_INACTIVE = 1;
	public static final byte STATUS_WATCHED = 11;
	
	public List<String> findWatchedSymbols() throws SQLException {
		String sql = "select abbr_name from alt_coin where status = " + STATUS_WATCHED ;// + " limit 1"
		
		ColumnListHandler<String> handler = new ColumnListHandler<>(1);
		
		List<String> symbols = DbManager.inst().query(sql, handler);
		
//		symbols = new ArrayList<>();
//		symbols.add("VRC");
		
		return symbols;
	}
	
	public static void main(String[] args) throws SQLException {
		AltCoinService ser = new AltCoinService();
		System.out.println(ser.findWatchedSymbols());
	}
	
}

package org.omega.marketcrawler.db;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Utils;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public final class PoolUtils {

	private static final Log log = LogFactory.getLog(PoolUtils.class);
	
	private static final PoolUtils pu = new PoolUtils();
	private static final String PROP_RESOURCE_PATH = "bonecp-config.xml";
	
	private BoneCP pool = null;
//	private BoneCPDataSource dataSource = null;
	
	public static PoolUtils inst() {
		return pu;
	}
	
	private PoolUtils() {
		try {
			BoneCPConfig config = new BoneCPConfig();
			InputStream is = new FileInputStream(Utils.tryGetResourceFile(PROP_RESOURCE_PATH));
			config = new BoneCPConfig(is, "market");
			pool = new BoneCP(config);
//			dataSource = new BoneCPDataSource(config);
		} catch (Exception e) {
			log.error("init datasource error.", e);
		} 
	}
	
	public Connection getConnection() throws SQLException {
//		log.info("" + pool.getTotalCreatedConnections() + ", " + pool.getTotalFree() + ", " + pool.getTotalLeased());
//		StringBuilder sb = new StringBuilder("Pool info: ")
//		.append("Created[").append(pool.getTotalCreatedConnections()).append("],")
//		.append("Free[").append(pool.getTotalFree()).append("],")
//		.append("Leased[").append(pool.getTotalLeased()).append("],")
//		.append("Requested[").append(pool.getStatistics().getConnectionsRequested()).append("],")
//		.append("WaitTimeAvg[").append(pool.getStatistics().getConnectionWaitTimeAvg()).append("].")
//		;
		
//		log.info(sb.toString());
		return pool.getConnection();
	}
	
	
	
}

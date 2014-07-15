package org.omega.marketcrawler.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SystemWarmup {
	
	private static final Log log = LogFactory.getLog(SystemWarmup.class);
	
	private static final SystemWarmup sys = new SystemWarmup();
	
	private SystemWarmup() {}
	
	public static SystemWarmup inst() {
		return sys;
	}
	
	public void warmup() {
		
		
	}
	
}

package org.omega.marketcrawler.main;

import org.omega.marketcrawler.common.SystemWarmup;

public final class SystemLauncher {

	private SystemLauncher() {}
	
	public static void main(String[] args) {
		
		SystemWarmup.inst().warmup();
		
		
		
	}
	
}

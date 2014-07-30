package org.omega.marketcrawler.common;

public final class Constants {
	
	private Constants() {}
	
	public static final byte STATUS_ACTIVE = 0;
	public static final byte STATUS_INACTIVE = 1;
	public static final byte STATUS_WATCHED = 11;
	
	public static final int MILLIS_ONE_SECOND = 1 * 1000;
	public static final int MILLIS_ONE_MINUTE = 1 * 60 * MILLIS_ONE_SECOND;
	
	public static final String CRAWL_FOLDER = "/storage/crawler4j";
	public static final String CRAWL_PAGES_FOLDER = CRAWL_FOLDER + "/pages";
	
	public static final String TEXT_NULL = "null";
	
	public static final int BOARD_ID_ANN = 159;
	
}

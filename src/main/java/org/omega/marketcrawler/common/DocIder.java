package org.omega.marketcrawler.common;

import java.util.concurrent.atomic.AtomicInteger;

public class DocIder {
	
	private static Object lock = new Object();
	private static final AtomicInteger id = new AtomicInteger(1000);

	private DocIder() { }

	public static int getNext() {
		int nxt = 0;
		synchronized (lock) {
			nxt = id.getAndIncrement();
		}
		return nxt;
	}

}

package org.omega.marketcrawler.net;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpsClient {
	
	private static final Log log = LogFactory.getLog(HttpsClient.class);
	
	private static X509TrustManager tm = new EmptyX509TrustManager();


	
	
	

}

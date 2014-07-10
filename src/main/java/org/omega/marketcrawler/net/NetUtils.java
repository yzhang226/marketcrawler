package org.omega.marketcrawler.net;

import java.io.IOException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.omega.marketcrawler.exchange.Poloniex;

public final class NetUtils {
	
	private static final Log log = LogFactory.getLog(NetUtils.class);
	
	/**
	 * can access http and https
	 * @param url
	 * @return
	 */
	public static String accessDirectly(String url) throws Exception {
		String responseBody = null;
		CloseableHttpClient httpclient = null;
		
		if (url.startsWith("https")) {
			TrustManager[] tm = { new EmptyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
	        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} else {
			 httpclient = HttpClients.createDefault();
		}
		
		HttpGet httpget = new HttpGet(url);
		ResponseHandler<String> responseHandler = new PlainResponseHandler();
		try {
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (Exception e) {
			log.error("access URL[" + url + "] error.");
			throw e;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				log.error("Close Httpclient error", e);
			}
		}

		return responseBody;
	}
	
	// // https://poloniex.com:443/public?command=returnTicker
	public static String extractHost(String url) {
		String matched = "";
		String[] ss = url.split("/");
		if (url.startsWith("http")) {
			matched = ss[2];
		} else {
			matched = ss[0];
		}
		
		if (matched.contains(":")) {
			int colonIdx = matched.indexOf(':');
			matched = matched.substring(0, colonIdx);
		}
		
		return matched;
	}
	
	// https://poloniex.com:443/public?command=returnTicker  3
	// https://poloniex.com/public?command=returnTicker 2
	public static int extractPort(String url) {
		int port = 443;
		
		String matched = "";
		String[] ss = url.split("/");
		if (url.startsWith("http")) {
			matched = ss[2];
		} else {
			matched = ss[0];
		}
		
		if (matched.contains(":")) {
			int colonIdx = matched.indexOf(':');
			port = Integer.valueOf(matched.substring(colonIdx+1, matched.length()));
		} else {
			if (url.startsWith("https")) {
				port = 443;
			} else if (url.startsWith("http")) {
				port = 80;
			}
		}
	
		return port;
	}
	
	public static void main(String[] args) throws Exception {
		String url = "https://bittrex.com/api/v1/public/getmarkethistory?market=BTC-DOGE&count=5";
//		String content =  accessDirectly(url);
//		System.out.println(content);
		System.out.println("------------------------------------------------");
		url = "https://api.mintpal.com/v2/market/trades/MINT/BTC";
//		content =  accessDirectly(url);
//		System.out.println(content);
		
		System.out.println(extractHost(url));
		
		url = "http://poloniex.com/public?command=returnTicker";
		System.out.println(extractHost(url));
		System.out.println(extractPort(url));
		
//		System.out.println(accessDirectly(Mintpal.instance().getMarketSummaryAPI()));
		System.out.println(accessDirectly(Poloniex.instance().getMarketSummaryAPI()));
		
	}
	
}

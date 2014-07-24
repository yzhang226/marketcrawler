package org.omega.marketcrawler.net;

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.operator.Bittrex;
import org.omega.marketcrawler.operator.Mintpal;

public final class NetUtils {
	
	private static final Log log = LogFactory.getLog(NetUtils.class);
	
//	public static String post(String url) {
//		
//	}
	
	/**
	 * can access http and https
	 */
	public static String get(String url) throws Exception {
		String responseBody = null;
		CloseableHttpClient httpclient = null;
		HttpGet httpget = null;
		try {
//			if (url.startsWith("https")) {
//				TrustManager[] tm = { new EmptyX509TrustManager() };
//				SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
//				sslContext.init(null, tm, new java.security.SecureRandom());
//		        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//				
//				httpclient = HttpClients.custom().disableAutomaticRetries().setSSLSocketFactory(sslsf).build();
//			} else {
				httpclient = HttpClients.custom().disableAutomaticRetries().build();
//			}
			
			httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = new PlainResponseHandler();
			
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (Throwable e) {
			String error = "access URL[" + url + "] error.";
//			log.error(error, e);
			throw new Exception(error, e);
		} finally {
			httpget.releaseConnection();
			try {
				httpclient.close();
			} catch (Throwable e) {
				log.error("Close Httpclient error", e);
			}
		}

		return responseBody;
	}
	
	public SSLConnectionSocketFactory createSSLFactory() throws Exception {
		TrustManager[] tm = { new EmptyX509TrustManager() };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
        return new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
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
	
	public static void main(String[] args) throws Throwable {
//		String url = "https://bittrex.com/api/v1/public/getmarkethistory?market=BTC-DOGE&count=5";
//		String content =  accessDirectly(url);
//		System.out.println(content);
		System.out.println("------------------------------------------------");
//		url = "https://api.mintpal.com/v2/market/trades/MINT/BTC";
//		content =  accessDirectly(url);
//		System.out.println(content);
		
//		System.out.println(extractHost(url));
		
//		url = "http://poloniex.com/public?command=returnTicker";
//		System.out.println(extractHost(url));
//		System.out.println(extractPort(url));
		
//		System.out.println(accessDirectly(Mintpal.instance().getMarketSummaryAPI()));
//		System.out.println(get(Poloniex.instance().getMarketSummaryAPI()));
		WatchListItem item = new WatchListItem("bittrex", "VAST", "BTC");
		System.out.println(get(Bittrex.instance().getMarketTradeAPI(item)));
		
//		item = new WatchListItem("mintpal", "VRC", "BTC");
//		System.out.println(get(Mintpal.instance().getMarketTradeAPI(item)));
	}
	
}

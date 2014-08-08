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

public final class NetUtils {
	
	private static final Log log = LogFactory.getLog(NetUtils.class);
	
	/**
	 * can access http and https
	 */
	public static String get(String url) throws Exception {
		String responseBody = null;
		CloseableHttpClient httpclient = null;
		HttpGet httpget = null;
		try {
			
			httpclient = HttpClients.custom().disableAutomaticRetries().setSSLSocketFactory(createSSLFactory()).build();
			
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
	
	public static SSLConnectionSocketFactory createSSLFactory() throws Exception {
		TrustManager[] tm = { new EmptyX509TrustManager() };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
        return new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	}
	
	public static void main(String[] args) throws Throwable {
		WatchListItem item = new WatchListItem("bittrex", "VAST", "BTC");
		item = new WatchListItem("poloniex", "VRC", "BTC");
		
		System.out.println(NetUtils.get("https://bitcointalk.org/index.php?topic=708714.0"));
	}
	
}

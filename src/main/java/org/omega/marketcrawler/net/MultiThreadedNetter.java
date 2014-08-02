package org.omega.marketcrawler.net;

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.omega.marketcrawler.operator.Poloniex;


/**
 * access url by using common <code>httpclient</code>
 * @author cook
 *
 */
public final class MultiThreadedNetter {

	private static final Log log = LogFactory.getLog(MultiThreadedNetter.class);
	
	private static final int CONN_TIMEOUT_MS = 2 * 1000;
	private static final int CONN_REQUEST_TIMEOUT_MS = 2 * 1000;
	private static final int SOCKET_TIMEOUT_MS = 3 * 1000;
	
	private static final Object lock = new Object();
	private static final MultiThreadedNetter netter = new MultiThreadedNetter();
	
	private CloseableHttpClient httpclient = null;
	private CloseableHttpClient retriesHttpclient = null;
	
	private MultiThreadedNetter() {}
	
	public static MultiThreadedNetter inst() {
		return netter;
	}
	
	public void reinit() throws Exception {
		reinit(2, 20);// default
	}
	
	public void reinit(int maxConnPerRoute, int maxConnTotal) throws Exception {
		log.info("Start Reinit MultiThreadedNetter.");
		try {
			close();
		} catch (Exception e) {
			log.error("close httpclient error.", e);
		}
		
		try {
	        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(buildSocketFactoryRegistry());
			httpclient = HttpClients.custom().disableAutomaticRetries()//.setSSLSocketFactory(createSSLFactory())
									.setConnectionManager(cm)
									.setDefaultRequestConfig(buildRequestConfig(CONN_TIMEOUT_MS*3, CONN_REQUEST_TIMEOUT_MS*3, SOCKET_TIMEOUT_MS*2))
									.setMaxConnPerRoute(maxConnPerRoute)
									.setMaxConnTotal(maxConnTotal)
									.build();
			
			PoolingHttpClientConnectionManager cm2 = new PoolingHttpClientConnectionManager(buildSocketFactoryRegistry());
			retriesHttpclient = HttpClients.custom()
									.setConnectionManager(cm2)
									.setDefaultRequestConfig(buildRequestConfig(CONN_TIMEOUT_MS*3, CONN_REQUEST_TIMEOUT_MS*3, SOCKET_TIMEOUT_MS*2))
									.setMaxConnPerRoute(maxConnPerRoute)
									.setMaxConnTotal(maxConnTotal)
									.build();
		} catch (Throwable e) {
			throw new Exception("reinit httpclient error.", e);
		}
		
		log.info("End Reinit MultiThreadedNetter.");
	}

	public SSLConnectionSocketFactory createSSLFactory() throws Exception {
		TrustManager[] tm = { new EmptyX509TrustManager() };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
        return new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	}
	
	public RequestConfig buildRequestConfig() {
		return buildRequestConfig(CONN_TIMEOUT_MS, CONN_REQUEST_TIMEOUT_MS, SOCKET_TIMEOUT_MS);
	}
	
	public RequestConfig buildRequestConfig(int connTimoutMills, int connRequestTimeoutMills, int socketTimeoutMills) {
		return RequestConfig.custom()
				.setConnectTimeout(connTimoutMills)
				.setConnectionRequestTimeout(connRequestTimeoutMills)
				.setSocketTimeout(socketTimeoutMills).build();
	}
	
	public Registry<ConnectionSocketFactory> buildSocketFactoryRegistry() throws Exception {
		return RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", createSSLFactory())
	            .build();
	}
	
	public String get(String url) throws Exception {
		return _get(url, getHttpclient());
	}
	
	public String getWithRetries(String url) throws Exception {
		return _get(url, getRetriesHttpclient());
	}
	
	private String _get(String url, CloseableHttpClient httpclient) throws Exception {
		String responseBody = null;
		HttpGet httpget = null;
		try {
			httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = new PlainResponseHandler();
			
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (Throwable e) {
			String error = "access URL[" + url + "] error.";
			throw new Exception(error, e);
		} finally {
			if (httpget != null) httpget.releaseConnection();
		}

		return responseBody;
	}
	
	public CloseableHttpClient getRetriesHttpclient() throws Exception {
		if (retriesHttpclient == null) {
			synchronized (lock) {
				if (retriesHttpclient == null) { reinit(); }
			}
		}
		return retriesHttpclient;
	}

	public CloseableHttpClient getHttpclient() throws Exception {
		if (httpclient == null) {
			synchronized (lock) {
				if (httpclient == null) { reinit(); }
			}
		}
		return httpclient;
	}
	
	public void close() {
		if (httpclient != null) {
			synchronized (lock) {
				if (httpclient != null) {
					try {
						httpclient.close();
					} catch (Exception e) {
						log.error("close httpclient error.", e);
					} finally {
						httpclient = null;
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		MultiThreadedNetter netter = MultiThreadedNetter.inst();
		netter.reinit();
		String resu = netter.get(Poloniex.instance().getMarketSummaryAPI());
		System.out.println(resu);
		netter.close();
	}
	
}

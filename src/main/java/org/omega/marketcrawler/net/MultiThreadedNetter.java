package org.omega.marketcrawler.net;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ResponseHandler;
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

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.*;


/**
 * access url by using common <code>httpclient</code>
 * @author cook
 *
 */
public final class MultiThreadedNetter {

	private static final Log log = LogFactory.getLog(MultiThreadedNetter.class);
	
	private static final Object lock = new Object();
	
	private static final MultiThreadedNetter netter = new MultiThreadedNetter();
	
	private CloseableHttpClient httpclient = null;
	
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
//	        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", createSSLFactory())
            .build();
	        
	        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
			httpclient = HttpClients.custom().disableAutomaticRetries()//.setSSLSocketFactory(createSSLFactory())
									.setConnectionManager(cm)
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
	
	public String get(String url) throws Exception {
		String responseBody = null;
		HttpGet httpget = null;
		try {
			httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = new PlainResponseHandler();
			
			responseBody = getHttpclient().execute(httpget, responseHandler);
		} catch (Throwable e) {
			String error = "access URL[" + url + "] error.";
			throw new Exception(error, e);
		} finally {
			if (httpget != null) httpget.releaseConnection();
		}

		return responseBody;
	}
	
	public CloseableHttpClient getHttpclient() throws Exception {
		if (httpclient == null) {
			synchronized (lock) {
				if (httpclient == null) { reinit(); }
			}
		}
		
		return httpclient;
	}
	
	public void close() throws Exception {
		try {
			if (httpclient != null) httpclient.close();
			httpclient = null;
		} catch (Throwable e) {
			throw new Exception("close httpclient error.", e);
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

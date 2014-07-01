package org.omega.marketcrawler.net;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public final class NetUtils {
	
	private static final Log log = LogFactory.getLog(NetUtils.class);
	
	/**
	 * can access http and https
	 * @param url
	 * @return
	 */
	public static String accessDirectly(String url) {
		String responseBody = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = new PlainResponseHandler();
			
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (Exception e) {
			log.error("Access URL[" + url + "] error.", e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				log.error("Close Httpclient error", e);
			}
		}

		return responseBody;
	}
	
	public static void main(String[] args) throws Exception {
		String url = "https://bittrex.com/api/v1/public/getmarkethistory?market=BTC-DOGE&count=5";
		String content =  accessDirectly(url);
		System.out.println(content);
		System.out.println("------------------------------------------------");
		url = "https://api.mintpal.com/v2/market/trades/MINT/BTC";
		content =  accessDirectly(url);
		System.out.println(content);
	}
	
}

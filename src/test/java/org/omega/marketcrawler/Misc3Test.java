package org.omega.marketcrawler;

import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.net.NetUtils;
import org.omega.marketcrawler.operator.Cryptsy;

public class Misc3Test {

	
	public static void main(String[] args) throws Exception {
		// 32_cryptsy_btc_via_261
		WatchListItem item = new WatchListItem("cryptsy", "via", "btc");
//		item.setMarketId(261);
//		System.out.println(NetUtils.get(Cryptsy.instance().getMarketTradeAPI(item)));
		
		// 29_cryptsy_btc_key_255
		item = new WatchListItem("cryptsy", "key", "btc");
		item.setMarketId(255);
		System.out.println(NetUtils.get(Cryptsy.instance().getMarketTradeAPI(item)));
		
	}
}

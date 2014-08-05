package org.omega.marketcrawler;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.omega.marketcrawler.entity.MarketTrade;
import org.omega.marketcrawler.entity.WatchListItem;
import org.omega.marketcrawler.net.NetUtils;
import org.omega.marketcrawler.operator.Bittrex;
import org.omega.marketcrawler.operator.Operator;
import org.omega.marketcrawler.operator.OperatorFactory;
import org.omega.marketcrawler.service.MarketTradeService;

public class ResultComparer {

	static final String compare_dir = "/Users/cook/Downloads/compare";
	private MarketTradeService mtser = new MarketTradeService();
	
	
	public static void main(String[] args) throws Exception {
		ResultComparer comparer = new ResultComparer();
		
		WatchListItem item = new WatchListItem("bittrex", "key", "BTC");
		comparer.compareDbAndServerJJson(item, Bittrex.MAX_LIMIT);
		
//		item = new WatchListItem("mintpal", "gue", "BTC");
//		comparer.compareDbAndServerJJson(item, Mintpal.DEFAULT_LIMIT);
		
		
		item = new WatchListItem("cryptsy", "icb", "BTC");
		item.setMarketId(267);
//		comparer.compareDbAndServerJJson(item, Cryptsy.DEFAULT_LIMIT);
		
		item = new WatchListItem("poloniex", "key", "BTC");
//		comparer.compareDbAndServerJJson(item, Poloniex.DEFAULT_LIMIT);
		
	}
	
	public String getTopSql(String table, int limit) {
		return "select * from " + table + " order by trade_time desc, trade_id desc, nano_time desc limit " + limit;
	}
	
	public String getDbOutFile(String table) {
		return compare_dir + "/" + table + ".db.json.txt";
	}
	
	public String getServerOutFile(String table) {
		return compare_dir + "/" + table + ".server.json.txt";
	}
	
	public void compareDbAndServerJJson(WatchListItem item, int limit) throws Exception {
		List<MarketTrade> mts = mtser.find(getTopSql(item.toMarketTradeTable(), limit));
		
		Operator op = OperatorFactory.get(item.getOperator());
		StringBuilder sb = new StringBuilder();
		for (MarketTrade mt : mts) {
			sb.append(op.reverseToJson(mt)).append(",\n");
		}
		IOUtils.write(sb.toString().getBytes(), new FileOutputStream(getDbOutFile(item.toMarketTradeTable())));
		
		String serverJson = NetUtils.get(op.getMarketTradeAPI(item));
		serverJson = serverJson.replaceAll("\\},", "},\n");
		serverJson = serverJson.substring(serverJson.indexOf("[{")+1);
		IOUtils.write(serverJson.toString().getBytes(), new FileOutputStream(getServerOutFile(item.toMarketTradeTable())));
	}
	
	
	
	
	
}

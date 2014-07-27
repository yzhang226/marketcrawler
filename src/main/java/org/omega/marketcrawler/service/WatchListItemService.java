package org.omega.marketcrawler.service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.db.DbManager;
import org.omega.marketcrawler.entity.WatchListItem;

public class WatchListItemService {

	private static final Map<String, String> columnToProperty = new HashMap<String, String>();
	
	private static final String INSERT_SQL = "INSERT INTO watch_list_item (status,operator,watched_symbol,exchange_symbol,market_id) VALUES (?,?,?,?,?)";
	private static final String UPDATE_STATUS_SQL = "UPDATE watch_list_item SET status = ? WHERE id = ?";
	
	static {
		columnToProperty.put("watched_symbol", "watchedSymbol");
		columnToProperty.put("exchange_symbol", "exchangeSymbol");
		columnToProperty.put("market_id", "marketId");
	}
	
	
	private Object[] convertBeanPropertiesToArray(WatchListItem item) {
		return new Object[]{item.getStatus(), item.getOperator(), item.getWatchedSymbol(), item.getExchangeSymbol(), item.getMarketId()};
	}
	
	public List<WatchListItem> findAll() throws SQLException {
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new BeanProcessor(columnToProperty));
		BeanListHandler<WatchListItem> handler = new BeanListHandler<>(WatchListItem.class, rowProcessor);
		
		return DbManager.inst().query("select * from watch_list_item",  handler);
	}
	
	public List<WatchListItem> findActiveItems() throws SQLException {
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new BeanProcessor(columnToProperty));
		BeanListHandler<WatchListItem> handler = new BeanListHandler<>(WatchListItem.class, rowProcessor);
		
		return DbManager.inst().query("select * from watch_list_item where status = 0",  handler);
	}
	
	protected int save(WatchListItem item) throws SQLException {
		Object[] params = convertBeanPropertiesToArray(item);
		return DbManager.inst().execute(INSERT_SQL, params);
	}
	
	protected int updateStatus(WatchListItem item) throws SQLException {
		return DbManager.inst().execute(UPDATE_STATUS_SQL, item.getStatus(), item.getId());
	}
	
	@SuppressWarnings("unchecked")
	public void initWatchedItem() throws SQLException {
		List<WatchListItem> dbItems = findAll();
		
		List<String> watchedSymbols = new AltCoinService().findWatchedSymbols();
		List<WatchListItem> watchedItems = new MarketSummaryService().findWatchedItems(watchedSymbols);
		
		if (Utils.isNotEmpty(watchedItems) && Utils.isNotEmpty(dbItems)) {
			Collection<WatchListItem> activeItems = CollectionUtils.intersection(dbItems, watchedItems);
			Collection<WatchListItem> inactiveItems = CollectionUtils.subtract(dbItems, watchedItems);
			Collection<WatchListItem> addedItems = CollectionUtils.subtract(watchedItems, dbItems);
			for (WatchListItem it : activeItems) {
				it.setStatus(Constants.STATUS_ACTIVE);
				updateStatus(it);
			}
			for (WatchListItem it : inactiveItems) {
				it.setStatus(Constants.STATUS_INACTIVE);
				updateStatus(it);
			}
			for (WatchListItem it : addedItems) {
				it.setStatus(Constants.STATUS_ACTIVE);
				save(it);
			}
		} else if (Utils.isEmpty(watchedItems) && Utils.isNotEmpty(dbItems)) {// no watched items
			for (WatchListItem it : dbItems) {
				it.setStatus(Constants.STATUS_INACTIVE);
				updateStatus(it);
			}
		} else if (Utils.isNotEmpty(watchedItems) && Utils.isEmpty(dbItems)) {// all watched items
			for (WatchListItem it : watchedItems) {
				it.setStatus(Constants.STATUS_ACTIVE);
				save(it);
			}
		} else {// both empty
			
		}
		
	}
	
	
	public static void main(String[] args) throws SQLException {
		
		WatchListItemService wiser = new WatchListItemService();
//		wiser.initWatchedItem();
		
		for (WatchListItem item : wiser.findActiveItems()) {
			StringBuilder sb = new StringBuilder(" ALTER TABLE ");
			sb.append(item.toMarketTradeTable())
			  .append(" CHANGE COLUMN price price FLOAT(16,8) NULL DEFAULT NULL  , CHANGE COLUMN total_units total_units FLOAT(16,8) NULL DEFAULT NULL  , CHANGE COLUMN total_cost total_cost FLOAT(16,8) NULL DEFAULT NULL;");
			System.out.println(sb.toString());
			/*
			 * ALTER TABLE trade_bittrex_btc_key CHANGE COLUMN price price FLOAT(16,8) NULL DEFAULT NULL  , CHANGE COLUMN total_units total_units FLOAT(16,8) NULL DEFAULT NULL  , CHANGE COLUMN total_cost total_cost FLOAT(16,8) NULL DEFAULT NULL  ;

			 */
		}
		
	}
	
}

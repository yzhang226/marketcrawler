package org.omega.marketcrawler.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.entity.AltCoin;
import org.omega.marketcrawler.entity.WatchListItem;

public class AltCoinService {

	private static final Log log = LogFactory.getLog(AltCoinService.class);
	
	private static final Map<String, String> columnToProperty = new HashMap<String, String>();
	
	private static final String INSERT_SQL = "INSERT INTO alt_coin (topic_id, status, interest, author, title, replies, views, link, publish_content, launch_time, last_post_time, publish_date, create_time, name, abbr_name, algo, proof, launch_raw, total_amount, block_time, half_blocks, half_days, block_reward, difficulty_adjust, pre_mined, mined_percentage, pow_days, pow_height, pow_amount, memo)"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_SQL = "UPDATE alt_coin SET topic_id=?, status=?, interest=?, author=?, title=?, replies=?, views=?, link=?, "
			+ "publish_content=?, launch_time=?, last_post_time=?, publish_date=?, create_time=?, name=?, abbr_name=?, algo=?, proof=?, launch_raw=?, "
			+ "total_amount=?, block_time=?, half_blocks=?, half_days=?, block_reward=?, difficulty_adjust=?, pre_mined=?, mined_percentage=?, pow_days=?, "
			+ "pow_height=?, pow_amount=?, memo=?"
			+ " WHERE id = ?";
	
	static {
		columnToProperty.put("topicId", "topic_id");
		columnToProperty.put("publishContent", "publish_content");
		
		columnToProperty.put("launchTime", "launch_time");
		columnToProperty.put("lastPostTime", "last_post_time");
		columnToProperty.put("publishDate", "publish_date");
		columnToProperty.put("createTime", "create_time");
		columnToProperty.put("abbrName", "abbr_name");
		columnToProperty.put("launchRaw", "launch_raw");
		
		columnToProperty.put("total_amount", "totalAmount");
		columnToProperty.put("blockTime", "block_time");
		columnToProperty.put("halfBlocks", "half_blocks");
		columnToProperty.put("halfDays", "half_days");
		columnToProperty.put("blockReward", "block_reward");
		columnToProperty.put("difficultyAdjust", "difficulty_adjust");
		columnToProperty.put("preMined", "pre_mined");
		columnToProperty.put("minedPercentage", "mined_percentage");
		
		columnToProperty.put("powDays", "pow_days");
		columnToProperty.put("powHeight", "pow_height");
		columnToProperty.put("powAmount", "pow_amount");
	}
	
	
	private Object[] convertBeanPropertiesToArray(AltCoin co) {
		/**
		 * 
	co.topicId;
	co.status;
	co.interest;
	co.author;
	co.title;
	co.replies;
	co.views;
	co.link;
	co.publishContent;
	co.launchTime;
	co.lastPostTime;
	co.publishDate;
	co.createTime;
	co.name;
	co.abbrName;
	co.algo;
	co.proof;
	co.launchRaw;
	co.totalAmount;
	co.blockTime;
	co.halfBlocks;
	co.halfDays;
	co.blockReward;
	co.difficultyAdjust;
	co.preMined;
	co.minedPercentage;
	co.powDays;
	co.powHeight;
	co.powAmount;
	co.memo;
		 */
		return new Object[] {co.getTopicId(), co.getStatus(), co.getInterest(), co.getAuthor(), co.getTitle(), co.getReplies(), 
				co.getViews(), co.getLink(), co.getPublishContent(), co.getLaunchTime(), co.getLastPostTime(), co.getPublishDate(), co.getCreateTime(), 
				co.getName(), co.getAbbrName(), co.getAlgo(), co.getProof(), co.getLaunchRaw(), co.getTotalAmount(), co.getBlockTime(), co.getHalfBlocks(), co.getHalfDays(), 
				co.getBlockReward(), co.getDifficultyAdjust(), co.getPreMined(), co.getMinedPercentage(), co.getPowDays(), co.getPowHeight(), co.getPowAmount(), co.getMemo()};
	}
	
	public int[] save(List<AltCoin> coins) throws SQLException {
		Object[][] params = new Object[coins.size()][9];
		for (int i=0; i<coins.size(); i++) {
			params[i] = convertBeanPropertiesToArray(coins.get(i));
		}
		return DbManager.inst().batch(INSERT_SQL, params);
	}
	
	public int save(AltCoin co) throws SQLException {
		return DbManager.inst().execute(INSERT_SQL, convertBeanPropertiesToArray(co));
	}
	
	public int update(AltCoin co) throws SQLException {
		Object[] props = convertBeanPropertiesToArray(co);
		Object[] props2 = Arrays.copyOf(props, props.length+1);
		props2[props.length] = co.getId();
		return DbManager.inst().execute(UPDATE_SQL, props2);
	}
	
	public List<String> findWatchedSymbols() throws SQLException {
		String sql = "select abbr_name from alt_coin where status = " + Constants.STATUS_WATCHED ;// + " limit 1"
		ColumnListHandler<String> handler = new ColumnListHandler<>(1);
		List<String> symbols = DbManager.inst().query(sql, handler);
		
		return symbols;
	}
	
	public AltCoin getByTopicId(int topicId) throws SQLException {
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new BeanProcessor(columnToProperty));
		BeanHandler<AltCoin> handler = new BeanHandler<>(AltCoin.class, rowProcessor);
		return DbManager.inst().query("select * from alt_coin where topic_id = ?",  handler, topicId);
	}
	
	public List<Long> findAllTopicIds() throws SQLException {
		String sql = "select topic_id from alt_coin " ;//
		ColumnListHandler<Long> handler = new ColumnListHandler<>(1);
		List<Long> topicIds = DbManager.inst().query(sql, handler);
		return topicIds;
	}
	
	public static void main(String[] args) throws SQLException {
		AltCoinService ser = new AltCoinService();
		System.out.println(ser.findWatchedSymbols());
//		int topicId = 442876;
		int topicId = 455854;
		AltCoin co = ser.getByTopicId(topicId);
		System.out.println(co.getId() + ", " + co.getAuthor() + ", " + co.getTopicId());
		
//		co.setAlgo("test");
//		
//		ser.update(co);
//		
//		co = ser.getByTopicId(topicId);
//		System.out.println("algo: " + co.getAlgo());
		
	}
	
}

package org.omega.marketcrawler.db;

import java.sql.SQLException;
import java.util.Arrays;
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

public class AltCoinService {

	private static final Log log = LogFactory.getLog(AltCoinService.class);
	
	private static final Map<String, String> columnToProperty = new HashMap<String, String>();
	
	private static final String INSERT_SQL = "INSERT INTO alt_coin (topic_id, status, interest, author, title, replies, views, publish_content, launch_time, "
			+ "last_post_time, publish_date, create_time, name, abbr_name, algo, proof, launch_raw, total_amount, block_time, half_blocks, half_days, block_reward, "
			+ "difficulty_adjust, pre_mined, mined_percentage, pow_days, pow_height, pow_amount, memo)"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			// + " ON DUPLICATE KEY UPDATE last_price=VALUES(last_price), ";
	private static final String UPDATE_SQL = "UPDATE alt_coin SET topic_id=?, status=?, interest=?, author=?, title=?, replies=?, views=?, "
			+ "publish_content=?, launch_time=?, last_post_time=?, publish_date=?, create_time=?, name=?, abbr_name=?, algo=?, proof=?, launch_raw=?, "
			+ "total_amount=?, block_time=?, half_blocks=?, half_days=?, block_reward=?, difficulty_adjust=?, pre_mined=?, mined_percentage=?, pow_days=?, "
			+ "pow_height=?, pow_amount=?, memo=?"
			+ " WHERE id = ?";
	
	static {
		columnToProperty.put("topic_id", "topicId");
		columnToProperty.put("publish_content", "publishContent");
		
		columnToProperty.put("launch_time", "launchTime");
		columnToProperty.put("last_post_time", "lastPostTime");
		columnToProperty.put("publish_date", "publishDate");
		columnToProperty.put("create_time", "createTime");
		columnToProperty.put("abbr_name", "abbrName");
		columnToProperty.put("launch_raw", "launchRaw");
		
		columnToProperty.put("totalAmount", "total_amount");
		columnToProperty.put("block_time", "blockTime");
		columnToProperty.put("half_blocks", "halfBlocks");
		columnToProperty.put("half_days", "halfDays");
		columnToProperty.put("block_reward", "blockReward");
		columnToProperty.put("difficulty_adjust", "difficultyAdjust");
		columnToProperty.put("pre_mined", "preMined");
		columnToProperty.put("mined_percentage", "minedPercentage");
		
		columnToProperty.put("pow_days", "powDays");
		columnToProperty.put("pow_height", "powHeight");
		columnToProperty.put("pow_amount", "powAmount");
	}
	
	
	private Object[] convertBeanPropertiesToArray(AltCoin co) {
		return new Object[] {co.getTopicId(), co.getStatus(), co.getInterest(), co.getAuthor(), co.getTitle(), co.getReplies(), 
				co.getViews(), co.getPublishContent(), co.getLaunchTime(), co.getLastPostTime(), co.getPublishDate(), co.getCreateTime(), 
				co.getName(), co.getAbbrName(), co.getAlgo(), co.getProof(), co.getLaunchRaw(), co.getTotalAmount(), co.getBlockTime(), co.getHalfBlocks(), co.getHalfDays(), 
				co.getBlockReward(), co.getDifficultyAdjust(), co.getPreMined(), co.getMinedPercentage(), co.getPowDays(), co.getPowHeight(), co.getPowAmount(), co.getMemo()
				};
	}
	
	private Object[] convertBeanPropertiesToArrayWithId(AltCoin co) {
		return new Object[] {co.getTopicId(), co.getStatus(), co.getInterest(), co.getAuthor(), co.getTitle(), co.getReplies(), 
				co.getViews(), co.getPublishContent(), co.getLaunchTime(), co.getLastPostTime(), co.getPublishDate(), co.getCreateTime(), 
				co.getName(), co.getAbbrName(), co.getAlgo(), co.getProof(), co.getLaunchRaw(), co.getTotalAmount(), co.getBlockTime(), co.getHalfBlocks(), co.getHalfDays(), 
				co.getBlockReward(), co.getDifficultyAdjust(), co.getPreMined(), co.getMinedPercentage(), co.getPowDays(), co.getPowHeight(), co.getPowAmount(), co.getMemo()
				, co.getId()};
	}
	
	public int[] save(List<AltCoin> coins) throws SQLException {
		Object[][] params = new Object[coins.size()][29];
		for (int i=0; i<coins.size(); i++) {
			params[i] = convertBeanPropertiesToArray(coins.get(i));
		}
		return DbManager.inst().batch(INSERT_SQL, params);
	}
	
	public int save(AltCoin co) throws SQLException {
		return DbManager.inst().execute(INSERT_SQL, convertBeanPropertiesToArray(co));
	}
	
	public int update(AltCoin co) throws SQLException {
		Object[] props = convertBeanPropertiesToArrayWithId(co);
		return DbManager.inst().execute(UPDATE_SQL, props);
	}
	
	public int[] update(List<AltCoin> cos) throws SQLException {
		Object[][] params = new Object[cos.size()][30];
		for (int i=0; i<cos.size(); i++) {
			params[i] = convertBeanPropertiesToArrayWithId(cos.get(i));
		}
		return DbManager.inst().batch(UPDATE_SQL, params);
	}
	
	public List<String> findWatchedSymbols() throws SQLException {
		String sql = "select abbr_name from alt_coin where status = ?";// + " limit 1"
		ColumnListHandler<String> handler = new ColumnListHandler<>(1);
		List<String> symbols = DbManager.inst().query(sql, handler, Constants.STATUS_WATCHED);
		
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
	
	public List<AltCoin> findAll() throws SQLException {
		return find("select * from alt_coin");
	}
	
	public List<AltCoin> find(String sql, Object... params) throws SQLException {
		BasicRowProcessor rowProcessor = new BasicRowProcessor(new BeanProcessor(columnToProperty));
		BeanListHandler<AltCoin> handler = new BeanListHandler<>(AltCoin.class, rowProcessor);
		return DbManager.inst().query(sql,  handler, params);
	}
	
	public static void main(String[] args) throws SQLException {
		AltCoinService ser = new AltCoinService();
		System.out.println(ser.findWatchedSymbols());
//		int topicId = 442876;
		int topicId = 455854;
//		int topicId = 656167;
		AltCoin co = ser.getByTopicId(topicId);
		System.out.println(co.getId() + ", " + co.getAuthor() + ", " + co.getTopicId());
		System.out.println(co.toReableText());
		co.setAlgo("testxxyyy");
		co.setTitle("okkkyyy");
		co.setMemo("with testyyy");
		
		ser.update(co);
//		ser.save(co);
		
		co = ser.getByTopicId(topicId);
		System.out.println("algo: " + co.getAlgo());
		System.out.println("title: " + co.getTitle());
		System.out.println(co.toReableText());
		
	}
	
}

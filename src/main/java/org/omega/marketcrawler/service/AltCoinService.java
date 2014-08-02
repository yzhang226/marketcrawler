package org.omega.marketcrawler.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.omega.marketcrawler.common.Constants;
import org.omega.marketcrawler.db.DbManager;
import org.omega.marketcrawler.entity.AltCoin;

public class AltCoinService extends SimpleDBService<AltCoin> {

	private static final Map<String, String> columnToProperty = new HashMap<String, String>();
	
	private static final String INSERT_SQL = "INSERT INTO alt_coin (my_topic_id, status, interest, launch_time, "
											+ "name, abbr_name, algo, proof, total_amount, block_time, half_blocks, half_days, block_reward, "
											+ "difficulty_adjust, pre_mined, mined_percentage, pow_days, pow_height, pow_amount, memo)"
											+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_SQL = "UPDATE alt_coin SET my_topic_id=?, status=?, interest=?, launch_time=?, name=?, abbr_name=?, algo=?, proof=?, "
											+ "total_amount=?, block_time=?, half_blocks=?, half_days=?, block_reward=?, difficulty_adjust=?, pre_mined=?, mined_percentage=?, pow_days=?, "
											+ "pow_height=?, pow_amount=?, memo=?"
											+ " WHERE id = ?";
	
	static {
		columnToProperty.put("topic_id", "topicId");
		columnToProperty.put("my_topic_id", "myTopicId");
		
		columnToProperty.put("launch_time", "launchTime");
		columnToProperty.put("abbr_name", "abbrName");
		
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
	
	@Override
	protected Map<String, String> getColumnToProperty() {
		return columnToProperty;
	}
	
	@Override
	protected String getTableName() {
		return "alt_coin";
	}
	
	private Object[] convertBeanPropertiesToArray(AltCoin co) {
		return new Object[] {co.getMyTopicId(), co.getStatus(), co.getInterest(), co.getLaunchTime(), 
				co.getName(), co.getAbbrName(), co.getAlgo(), co.getProof(), co.getTotalAmount(), co.getBlockTime(), co.getHalfBlocks(), co.getHalfDays(), 
				co.getBlockReward(), co.getDifficultyAdjust(), co.getPreMined(), co.getMinedPercentage(), co.getPowDays(), co.getPowHeight(), co.getPowAmount(), co.getMemo()
				};
	}
	
	private Object[] convertBeanPropertiesToArrayWithId(AltCoin co) {
		return new Object[] {co.getMyTopicId(), co.getStatus(), co.getInterest(), co.getLaunchTime(), 
				co.getName(), co.getAbbrName(), co.getAlgo(), co.getProof(), co.getTotalAmount(), co.getBlockTime(), co.getHalfBlocks(), co.getHalfDays(), 
				co.getBlockReward(), co.getDifficultyAdjust(), co.getPreMined(), co.getMinedPercentage(), co.getPowDays(), co.getPowHeight(), co.getPowAmount(), co.getMemo()
				, co.getId()};
	}
	
	public int[] save(List<AltCoin> coins) throws SQLException {
		Object[][] params = new Object[coins.size()][20];
		for (int i=0; i<coins.size(); i++) {
			params[i] = convertBeanPropertiesToArray(coins.get(i));
		}
		return executeBatch(INSERT_SQL, params);
	}
	
	public int save(AltCoin co) throws SQLException {
		return save(INSERT_SQL, convertBeanPropertiesToArray(co));
	}
	
	public int update(AltCoin co) throws SQLException {
		Object[] props = convertBeanPropertiesToArrayWithId(co);
		return update(UPDATE_SQL, props);
	}
	
	public int[] update(List<AltCoin> cos) throws SQLException {
		Object[][] params = new Object[cos.size()][21];
		for (int i=0; i<cos.size(); i++) {
			params[i] = convertBeanPropertiesToArrayWithId(cos.get(i));
		}
		return executeBatch(UPDATE_SQL, params);
	}
	
	public List<String> findWatchedSymbols() throws SQLException {
		String sql = "select abbr_name from alt_coin where status = ?";// + " limit 1"
		ColumnListHandler<String> handler = new ColumnListHandler<>(1);
		List<String> symbols = query(sql, handler, Constants.STATUS_WATCHED);
		
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
	
	public static void main(String[] args) throws SQLException {
		
		
	}
	
}

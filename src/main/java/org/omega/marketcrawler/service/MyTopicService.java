package org.omega.marketcrawler.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.omega.marketcrawler.common.Utils;
import org.omega.marketcrawler.entity.MyTopic;

public class MyTopicService extends SimpleDBService<MyTopic> {

	private static final String INSERT_SQL = "INSERT INTO my_topic (board_id, topic_id, author, title, replies, views, content, last_post_time, publish_time, create_time)"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_SQL = "UPDATE my_topic SET board_id=?, topic_id=?, author=?, title=?, replies=?, views=?, "
			+ "content=?, last_post_time=?, publish_time=?, create_time=?"
			+ " WHERE id = ?";
	private static final String UPDATE_PUBLISH_TIME_SQL = "UPDATE my_topic SET publish_time=? WHERE id = ?";
	
	static {
		columnToProperty.put("board_id", "boardId");
		columnToProperty.put("topic_id", "topicId");
		columnToProperty.put("last_post_time", "lastPostTime");
		columnToProperty.put("publish_time", "publishTime");
		columnToProperty.put("create_time", "createTime");
	}
	
	protected Map<String, String> getColumnToProperty() {
		return columnToProperty;
	}

	protected String getTableName() {
		return "my_topic";
	}
	
	protected Object[] objectToArray(MyTopic my) {
		return new Object[]{my.getBoardId(), my.getTopicId(), my.getAuthor(), my.getTitle(), my.getReplies(), my.getViews(), my.getContent(), my.getLastPostTime(), my.getPublishTime(), my.getCreateTime()};
	}
	
	protected Object[] objectToArray(MyTopic my, int currSeconds) {
		return new Object[]{my.getBoardId(), my.getTopicId(), my.getAuthor(), my.getTitle(), my.getReplies(), my.getViews(), my.getContent(), my.getLastPostTime(), my.getPublishTime(), currSeconds};
	}
	
	protected Object[] objectToArrayWithId(MyTopic my) {
		return new Object[]{my.getBoardId(), my.getTopicId(), my.getAuthor(), my.getTitle(), my.getReplies(), my.getViews(), my.getContent(), my.getLastPostTime(), my.getPublishTime(), my.getCreateTime(), my.getId()};
	}
	
	public int[] updatePublishTime(List<MyTopic> mys) throws SQLException {
		Object[][] params = new Object[mys.size()][2];
		for (int i=0; i<mys.size(); i++) {
			params[i][0] = mys.get(i).getPublishTime();
			params[i][1] = mys.get(i).getId();
		}
		return executeBatch(UPDATE_PUBLISH_TIME_SQL, params);
	}
	
	public int save(MyTopic my) throws SQLException {
		int resu = save(INSERT_SQL, objectToArray(my));
		return resu;
	}
	
	public int[] save(List<MyTopic> mys) throws SQLException {
		int createTime = Utils.changeMillsToSeconds(System.currentTimeMillis());
		Object[][] params = new Object[mys.size()][10];
		for (int i=0; i<mys.size(); i++) {
			params[i] = objectToArray(mys.get(i), createTime);
		}
		return executeBatch(INSERT_SQL, params);
	}
	
	public int[] update(List<MyTopic> mys) throws SQLException {
		Object[][] params = new Object[mys.size()][11];
		for (int i=0; i<mys.size(); i++) {
			params[i] = objectToArrayWithId(mys.get(i));
		}
		return executeBatch(UPDATE_SQL, params);
	}
	
}

package org.omega.marketcrawler.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.omega.marketcrawler.entity.MyTopic;

public class MyTopicService extends SimpleDBService<MyTopic> {

	private static final String INSERT_SQL = "INSERT INTO my_topic (board_id, topic_id, author, title, replies, views, content, last_post_time, publish_time, create_time)"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_SQL = "UPDATE my_topic SET board_id=?, topic_id=?, author=?, title=?, replies=?, views=?, "
			+ "content=?, last_post_time=?, publish_time=?, create_time=?"
			+ " WHERE id = ?";
	
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
	
	protected Object[] objectToArrayWithId(MyTopic my) {
		return new Object[]{my.getBoardId(), my.getTopicId(), my.getAuthor(), my.getTitle(), my.getReplies(), my.getViews(), my.getContent(), my.getLastPostTime(), my.getPublishTime(), my.getCreateTime(), my.getId()};
	}
	
	/**
	 * 
	 * @param my
	 * @return - with auto-generated id
	 * @throws SQLException
	 */
	public Integer save(MyTopic my) throws SQLException {
		int resu = save(INSERT_SQL, objectToArray(my));
		if (resu > 0) {
			Object[] re = queryUnique("select max(id) from " + getTableName());
			if (re != null && re[0] != null) {
				return (Integer) re[0];
			}
		}
		return null;
	}
	
	public int[] update(List<MyTopic> mys) throws SQLException {
		Object[][] params = new Object[mys.size()][11];
		for (int i=0; i<mys.size(); i++) {
			params[i] = objectToArrayWithId(mys.get(i));
		}
		return executeBatch(UPDATE_SQL, params);
	}
	
}

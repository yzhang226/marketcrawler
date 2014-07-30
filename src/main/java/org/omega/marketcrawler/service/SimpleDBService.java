package org.omega.marketcrawler.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.omega.marketcrawler.db.DbManager;

public abstract class SimpleDBService<E> {

	protected static final Map<String, String> columnToProperty = new HashMap<String, String>();
	
	protected Class<E> clz;
	protected DbManager dbManager;
	
	@SuppressWarnings("unchecked")
	public SimpleDBService() {
		 Type genType = getClass().getGenericSuperclass();
	     Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
	     clz = (Class<E>) params[0];
	     
	     dbManager = DbManager.inst();
	}
	
	protected abstract Map<String, String> getColumnToProperty();
	
	protected abstract String getTableName();
	
	protected BasicRowProcessor getRowProcessor() {
		return new BasicRowProcessor(new BeanProcessor(getColumnToProperty()));
	}
	
	protected BeanHandler<E> getBeanHandler() {
		return new BeanHandler<>(clz, getRowProcessor());
	}
	
	protected BeanListHandler<E> getBeanListHandler() {
		return new BeanListHandler<>(clz, getRowProcessor());
	}

	public <T> T query(String sql, ResultSetHandler<T> handler, Object... params) throws SQLException {
		return dbManager.query(sql, handler, params);
	}
	
	public List<E> find(String sql, Object... params) throws SQLException {
		return query(sql, getBeanListHandler(), params);
	}
	
	public E findUnique(String sql, Object... params) throws SQLException {
		return query(sql, getBeanHandler(), params);
	}
	
	public List<Object[]> query(String sql, Object... params) throws SQLException {
		ArrayListHandler handler = new ArrayListHandler();
		return query(sql, handler, params);
	}
	
	public Object[] queryUnique(String sql, Object... params) throws SQLException {
		ArrayHandler handler = new ArrayHandler();
		return query(sql, handler, params);
	}
	
	public List<E> findAll() throws SQLException {
		return findAll(getTableName());
	}
	
	public List<E> findAll(String tableName) throws SQLException {
		String sql = new StringBuilder("select * from ").append(tableName).toString();
		return find(sql);
	}
	
	public int[] executeBatch(String sql, Object[][] params) throws SQLException {
		return dbManager.batch(sql, params);
	}
	
	public int save(String sql, Object[] params) throws SQLException {
		return execute(sql, params);
	}
	
	public int update(String sql, Object[] params) throws SQLException {
		return execute(sql, params);
	}
	
	public int delete(String sql, Object... params) throws SQLException {
		return execute(sql, params);
	}
	
	public int execute(String sql, Object... params) throws SQLException {
		return dbManager.execute(sql, params);
	}
	
//	public void save(E entity) {
//		
//	}
	
}

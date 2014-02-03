package myframework.myjdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlSession {

	private Connection connection = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;

	SqlSession(Connection connection) {
		this.connection = connection;
	}

	public int delete(String sql, Object... args) throws SQLException {

		int result = 0;
		ps = connection.prepareStatement(sql);
		for (int i = 1; i <= args.length; i++) {
			ps.setObject(i, args[i - 1]);
		}
		result = ps.executeUpdate();
		return result;
	}

	public int insert(String sql, Object... args) throws SQLException {

		int result = 0;
		ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		for (int i = 1; i <= args.length; i++) {
			ps.setObject(i, args[i - 1]);
		}
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();

		if (rs.next())
			result = rs.getInt(1);
		return result;
	}

	public int update(String sql, Object... args) throws SQLException {
		int result = 0;

		ps = connection.prepareStatement(sql);
		for (int i = 1; i <= args.length; i++) {

			ps.setObject(i, args[i - 1]);
		}
		result = ps.executeUpdate();

		return result;
	}
	
	public List<Map<String,Object>> queryForList(String sql, Object... args) throws SQLException {

		List<Map<String,Object>> result = new ArrayList<>();
		ps = connection.prepareStatement(sql);
		for (int i = 0; i < args.length; i++) {
			ps.setObject(i, args[i]);
		}
		rs = ps.executeQuery();
		while (rs.next()) {
			Map<String,Object> rowMap = new HashMap<String, Object>();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnName(i);
				rowMap.put(columnName, rs.getObject(columnName));
			}
			result.add(rowMap);
		}
		return result;
	}
	
	
	public <T> List<T> query(String sql,Object[] args, RowMapper<T> rowMapper) throws SQLException {


		List<T> result = new ArrayList<>();
		ps = connection.prepareStatement(sql);
		for (int i = 0; i < args.length; i++) {
			ps.setObject(i, args[i]);
		}
		rs = ps.executeQuery();
		int rowNum = 0;
		while (rs.next()) {
			result.add(rowMapper.mapRow(rs, rowNum));
		}
		return result;
	}
	
	
	public int queryForInt(String sql, Object... args) throws SQLException {

		int result = 0;
		ps = connection.prepareStatement(sql);
		for (int i = 0; i < args.length; i++) {
			ps.setObject(i, args[i]);
		}
		rs = ps.executeQuery();
		
		if (rs.next()) {
			result = rs.getInt(1);
		}
		
		return result;
	}
	
	public <T> T execute(ConnectionCallback<T> action) throws SQLException {
		return action.doInConnection(connection);
	}
	

	void close() {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (connection != null) {
			try {
				connection.setAutoCommit(true);
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		connection.setAutoCommit(autoCommit);
	} 
	
	public void commit() throws SQLException {
		connection.commit();
	}
	public void rollback() throws SQLException {
		connection.rollback();
	}
	
}

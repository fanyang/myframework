package myframework.myjdbc;

import java.sql.SQLException;
import javax.sql.DataSource;

import myframework.datasource.MyDataSource;


/**
 * Provide ThreadLocal based JDBC session.
 * @author Fan
 *
 */
public class MyJdbcTemplate {

	private DataSource dataSource;
	
	public MyJdbcTemplate() {
		
	}
	
	public MyJdbcTemplate(MyDataSource myDataSource) {
		this.dataSource = myDataSource;
	}
	
	
	public DataSource getDataSource() {
		return dataSource;
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	private ThreadLocal<SqlSession> localSession = new ThreadLocal<>();
	

	
	public SqlSession getSession() throws SQLException {
		SqlSession session = localSession.get();
		if (session == null) {
			session = new SqlSession(dataSource.getConnection());
			localSession.set(session);
		}
		return session;
	}
	
	public void closeSession() {
		SqlSession session = localSession.get();
		if(session!=null) {
			session.close();
			localSession.remove();
		}
	}
	
	
}

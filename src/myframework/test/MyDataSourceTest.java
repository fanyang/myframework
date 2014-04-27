package myframework.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;





import java.util.List;

import myframework.datasource.MyDataSource;
import myframework.di.BeanFactory;
import myframework.myjdbc.BeanPropertyRowMapper;
import myframework.myjdbc.MyJdbcTemplate;
import myframework.myjdbc.SqlSession;

import org.junit.Test;

public class MyDataSourceTest {
	
	public static final String driverClassName = "com.mysql.jdbc.Driver";
	public static final String url = "jdbc:mysql://localhost:3306/itat_shop?useUnicode=true&characterEncoding=utf8";
	public static final String username = "itat";
	public static final String password = "itat123";
	
	private static MyDataSource dataSource;
	private static BeanFactory context = new BeanFactory("beans.xml"); // All test cases use same BeanFactory.
	
	static {
		dataSource = new MyDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setMaxActive(10);
		dataSource.init();
	}
	

	@Test
	public void testDataSource() {
		try {
			Connection connection = dataSource.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select * from t_user");
			while (rs.next()) {
				System.out.println(rs.getString("nickname"));
			}
			rs.close();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGetConnection() {
			try {
				for (int i = 0; i < 20; i++) {
					Connection connection = dataSource.getConnection();
					System.out.println(connection);
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	
	@Test
	public void testUpdate() {
		MyJdbcTemplate myJdbcTemplate = context.getBean("myJdbcTemplate", MyJdbcTemplate.class);
		SqlSession session;
		try {
			session = myJdbcTemplate.getSession();
			session.insert("insert into t_user (username, password, nickname, type)"
					+ "value (?, ?, ?, ?)", "wangwu", "123", "王五", "0");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myJdbcTemplate.closeSession();
		}
	}
	
	@Test
	public void testTx() {
		MyJdbcTemplate myJdbcTemplate = context.getBean("myJdbcTemplate", MyJdbcTemplate.class);
		SqlSession session;
		try {
			session = myJdbcTemplate.getSession();
			session.setAutoCommit(false);
			session.insert("insert into t_user (username, password, nickname, type)"
					+ "value (?, ?, ?, ?)", "wangwu1", "123", "王五1", "0");
			session.rollback();
			session.insert("insert into t_user (username, password, nickname, type)"
					+ "value (?, ?, ?, ?)", "wangwu2", "123", "王五2", "0");
			session.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myJdbcTemplate.closeSession();
		}
	}
	
	@Test
	public void testQueryDi() {
		MyJdbcTemplate myJdbcTemplate = context.getBean("myJdbcTemplate", MyJdbcTemplate.class);
		SqlSession session;
		try {
			session = myJdbcTemplate.getSession();
			List<User> users = session.query("select * from t_user"
					, new Object[]{}
					, new BeanPropertyRowMapper<User>(User.class));
			for (User u : users) {
				System.out.println(u);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myJdbcTemplate.closeSession();
		}
		
		
	}
	
	
	
	
}

package myframework.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Fix size connection pool
 * @author Fan
 *
 */
public class MyDataSource implements DataSource{
	
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private int maxActive;
	
	private BlockingQueue<Connection> connectionPool;
	private static final int CONNECTION_TIMEOUT = 1;

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	
	public void init(){
		try {
			Class.forName(driverClassName);
			connectionPool = new ArrayBlockingQueue<>(maxActive);
			for (int i = 0; i < maxActive; i++) {
				connectionPool.put(new MyConnectionHandler(this)
				.bind(DriverManager.getConnection(url, username, password)));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public Connection getConnection() throws SQLException {
		Connection connection = null;
		try {
			connection = connectionPool.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!connection.isValid(CONNECTION_TIMEOUT)) {
			connection = new MyConnectionHandler(this)
			.bind(DriverManager.getConnection(url, username, password));
		}
		
		return connection;
	}
	
	/**
	 * Put connection into connection queue
	 * @param connection
	 */
	void free(Connection connection) {
		if (connectionPool.contains(connection)) return; // To avoid close connection more than once
		try {
			connectionPool.put(connection);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return null;
	}


}

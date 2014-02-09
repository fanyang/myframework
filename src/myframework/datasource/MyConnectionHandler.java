package myframework.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;


/**
 * A proxy to real connection
 * @author Fan
 *
 */
class MyConnectionHandler implements InvocationHandler {
	
	private Connection realConnection;
	private MyDataSource dataSource;


	MyConnectionHandler(MyDataSource dataSource) {
		this.dataSource = dataSource;
	}

	Connection bind(Connection realConn) {
		this.realConnection = realConn;
		return (Connection) Proxy.newProxyInstance(
				this.getClass().getClassLoader()
				,new Class[]{Connection.class}
				,this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if ("close".equals(method.getName())) {
			this.dataSource.free((Connection) proxy);
			return null;
		} else {
			return method.invoke(this.realConnection, args);
		}
	}

}

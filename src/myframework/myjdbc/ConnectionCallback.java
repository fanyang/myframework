package myframework.myjdbc;

import java.sql.Connection;

/**
 * Provide connection to user
 * @author Fan
 *
 * @param <T>
 */
public interface ConnectionCallback<T> {
	
	 public T doInConnection(Connection con);
	 
}


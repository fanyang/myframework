package myframework.myjdbc;

import java.sql.Connection;

public interface ConnectionCallback<T> {
	 T doInConnection(Connection con);
}


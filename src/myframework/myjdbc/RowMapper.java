package myframework.myjdbc;

import java.sql.ResultSet;

/**
 * Map result set to object.
 * @author Fan
 *
 * @param <T>
 */
public interface RowMapper<T> {
	
	public T mapRow(ResultSet rs, int rowNum);
	
}

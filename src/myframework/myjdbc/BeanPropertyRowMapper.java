package myframework.myjdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Bean property row mapper
 * @author Fan
 *
 * @param <T> bean class
 */
public class BeanPropertyRowMapper<T> implements RowMapper<T> {

	private Class<T> mappedClass;
	
	public BeanPropertyRowMapper(Class<T> mappedClass) {
		this.mappedClass = mappedClass;
	}
	
	@Override
	public T mapRow(ResultSet rs, int rowNum) {
		T bean = null;
		try {
			bean = mappedClass.newInstance();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnName(i);
				BeanUtils.copyProperty(bean, columnName, rs.getObject(columnName));
			}
		} catch (InstantiationException 
				| IllegalAccessException 
				| SQLException 
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return bean;
	}

}

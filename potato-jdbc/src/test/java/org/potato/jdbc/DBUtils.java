package org.potato.jdbc;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DBUtils {

	public static JdbcTemplateExt getJdbcTemplateExt() {
		
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/spring-jdbc-ext");
		dataSource.setUsername("postgres");
		dataSource.setPassword("elephant");
		
		JdbcTemplateExt extJdbcTemplate = new JdbcTemplateExt(dataSource);
		return extJdbcTemplate;
	}
}

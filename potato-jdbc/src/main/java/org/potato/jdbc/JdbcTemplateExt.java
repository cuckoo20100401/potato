package org.potato.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.util.web.PageInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import net.sf.jsqlparser.JSQLParserException;

public class JdbcTemplateExt extends JdbcTemplate {

	private static final Logger logger = LogManager.getLogger(JdbcTemplateExt.class);
	
	private String databaseProductName;
	
	public JdbcTemplateExt(DataSource dataSource) {
		super(dataSource);
		this.databaseProductName = this.getDatabaseProductName();
	}
	
	public <T> int insert(EntityPersistHelper<T> entityPersistHelper) {
		String sql = entityPersistHelper.getInsertSQL();
		List<Object> args = entityPersistHelper.getInsertArgs();
		logger.debug(sql);
		logger.debug(args);
    	return super.update(sql, args.toArray());
    }
    
    public <T> int update(EntityPersistHelper<T> entityPersistHelper) {
    	String sql = entityPersistHelper.getUpdateSQL();
		List<Object> args = entityPersistHelper.getUpdateArgs();
		logger.debug(sql);
		logger.debug(args);
		return super.update(sql, args.toArray());
    }

	public <T> int updateSelective(EntityPersistHelper<T> entityPersistHelper) {
		String sql = entityPersistHelper.getUpdateSelectiveSQL();
		List<Object> args = entityPersistHelper.getUpdateSelectiveArgs();
		logger.debug(sql);
		logger.debug(args);
		return super.update(sql, args.toArray());
	}

	public PageInfo<Map<String, Object>> findList(String sql, Object[] args, int pageNum, int pageSize) {

		SQLParser sqlParser = null;
		try {
			sqlParser = new SQLParser(databaseProductName, sql, pageNum, pageSize);
		} catch (JSQLParserException e) {
			throw new DataAccessException(sql, e) {
				private static final long serialVersionUID = 729049149151878653L;
			};
		}

		String paginationSQL = sqlParser.getPaginationSQL();
		String totalSQL = sqlParser.getTotalSQL();
		logger.debug(paginationSQL);
		if (args != null) logger.debug(Arrays.asList(args));
		logger.debug(totalSQL);

		List<Map<String, Object>> list = super.queryForList(paginationSQL, args);
		long total = super.queryForObject(totalSQL, args, Long.class);

		return new PageInfo<>(pageNum, pageSize, list, total);
	}
	
	public <T> PageInfo<T> findList(String sql, Object[] args, int pageNum, int pageSize, RowMapper<T> rowMapper) {
		
		SQLParser sqlParser = null;
		try {
			sqlParser = new SQLParser(databaseProductName, sql, pageNum, pageSize);
		} catch (JSQLParserException e) {
			throw new DataAccessException(sql, e) {
				private static final long serialVersionUID = 729049149151878652L;
			};
		}

		String paginationSQL = sqlParser.getPaginationSQL();
		String totalSQL = sqlParser.getTotalSQL();
		logger.debug(paginationSQL);
		if (args != null) logger.debug(Arrays.asList(args));
		logger.debug(totalSQL);
		
		List<T> list = super.query(paginationSQL, args, rowMapper);
		long total = super.queryForObject(totalSQL, args, Long.class);
		
		return new PageInfo<>(pageNum, pageSize, list, total);
	}

	public String getDatabaseProductName() {
		String databaseProductName = null;
		try {
			Connection conn = this.getDataSource().getConnection();
			DatabaseMetaData databaseMetaData = conn.getMetaData();
			databaseProductName = databaseMetaData.getDatabaseProductName();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return databaseProductName;
	}
}

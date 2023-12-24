package org.potato.util.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBC {
	
	private String url;
	private String username;
	private String password;
	
	public JDBC(String driverClassName, String url, String username, String password) throws ClassNotFoundException {
		this.url = url;
		this.username = username;
		this.password = password;
		Class.forName(driverClassName);
	}
	
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
	
	public String getDatabaseProductName() {
		String databaseProductName = null;
		try {
			Connection conn = this.getConnection();
			DatabaseMetaData databaseMetaData = conn.getMetaData();
			databaseProductName = databaseMetaData.getDatabaseProductName();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return databaseProductName;
	}
	
	public List<String> getAllTables() throws SQLException {
		
		List<String> tableNames = new ArrayList<>();
		
		Connection conn = this.getConnection();
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getTables(conn.getCatalog(), conn.getSchema(), "%", new String[]{"TABLE"});
		while (rs.next()) {
			tableNames.add(rs.getString("TABLE_NAME"));
		}
		rs.close();
		conn.close();
		return tableNames;
	}
	
	public List<Map<String, String>> getAllColumns(String tableName) throws SQLException {
		
		List<Map<String, String>> columns = new ArrayList<>();
		
		Connection conn = this.getConnection();
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getColumns(conn.getCatalog(), conn.getSchema(), tableName, "%");
		while (rs.next()) {
			Map<String, String> column = new HashMap<>();
			column.put("column_name", rs.getString("COLUMN_NAME"));
			column.put("column_type", rs.getString("TYPE_NAME"));
			column.put("column_description", rs.getString("REMARKS"));
			columns.add(column);
		}
		rs.close();
		conn.close();
		return columns;
	}
}

package org.potato.jdbc;

import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

public class SQLParser {
	
	private String paginationSQL;
	private String totalSQL;
	
	public SQLParser(String databaseProductName, String sql, int pageNum, int pageSize) throws JSQLParserException {
		this.parse(databaseProductName, sql, pageNum, pageSize);
	}

	@SuppressWarnings("unused")
	private void parse(String databaseProductName, String sql, int pageNum, int pageSize) throws JSQLParserException {
		
		String srcSql = null;
		String srcSqlSelectItems = null;
		String srcSqlFromItem = null;
		String srcSqlJoins = null;
		String srcSqlWhereExpression = null;
		String srcSqlOrderBy = null;
		
		// parse
		Statement statement = CCJSqlParserUtil.parse(sql);
		if (statement instanceof Select) {
			Select selectStatement = (Select) statement;
			PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
			//srcSql
			srcSql = selectStatement.toString();
			//srcSqlSelectItems
			StringBuilder sb = new StringBuilder();
			List<SelectItem> selectItems = plainSelect.getSelectItems();
			for (SelectItem selectItem: selectItems) {
				if (sb.length() == 0) {
					sb.append(selectItem.toString());
				} else {
					sb.append(", ").append(selectItem.toString());
				}
			}
			srcSqlSelectItems = sb.toString();
			//srcSqlFromItem
			srcSqlFromItem = plainSelect.getFromItem().toString();
			//srcSqlJoins
			List<Join> joins = plainSelect.getJoins();
			if (joins != null) {
				for (Join join: joins) {
					if (srcSqlJoins == null) {
						srcSqlJoins = join.toString();
					} else {
						srcSqlJoins += " "+join.toString();
					}
				}
			}
			//srcSqlWhere
			Expression expression = plainSelect.getWhere();
			if (expression != null) {
				srcSqlWhereExpression = expression.toString();
			}
			//srcSqlOrderBy
			List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
			if (orderByElements != null) {
				for (OrderByElement orderByElement: orderByElements) {
					if (srcSqlOrderBy == null) {
						srcSqlOrderBy = orderByElement.toString();
					} else {
						srcSqlOrderBy += ", "+orderByElement.toString();
					}
				}
			}
		} else {
			throw new JSQLParserException("The SQL is not a SELECT statement: "+sql);
		}
		
		// build this.paginationSQL
		if (databaseProductName.equals("PostgreSQL")) {
			this.paginationSQL = srcSql + " LIMIT " + pageSize + " offset " + (pageNum-1)*pageSize;
		} else if (databaseProductName.equals("MySQL")) {
			this.paginationSQL = srcSql + " LIMIT " + (pageNum-1)*pageSize + "," + pageSize;
		} else {
			throw new JSQLParserException("This database is not supported: "+databaseProductName);
		}
		
		// build this.totalSQL
		StringBuilder totalSQLBuilder = new StringBuilder();
		totalSQLBuilder.append("SELECT COUNT(*) FROM ").append(srcSqlFromItem);
		if (srcSqlJoins != null) {
			totalSQLBuilder.append(" ").append(srcSqlJoins);
		}
		if (srcSqlWhereExpression != null) {
			totalSQLBuilder.append(" WHERE ").append(srcSqlWhereExpression);
		}
		/*if (srcSqlOrderBy != null) {
			totalSQLBuilder.append(" ORDER BY ").append(srcSqlOrderBy);
		}*/
		this.totalSQL = totalSQLBuilder.toString();
	}

	public String getPaginationSQL() {
		return paginationSQL;
	}

	public String getTotalSQL() {
		return totalSQL;
	}
	
}

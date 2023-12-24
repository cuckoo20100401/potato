package org.potato.jdbc;

import org.potato.util.db.EntityTableTransformUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityPersistHelper<T> {
	
	private T entity;
	private String insertSQL;
	private String updateSQL;
	private String updateSelectiveSQL;
	private List<Object> insertArgs;
	private List<Object> updateArgs;
	private List<Object> updateSelectiveArgs;
	
	public EntityPersistHelper(T entity) {
		this.entity = entity;
	}
	
	public static <T> EntityPersistHelper<T> build(T entity) {
		return new EntityPersistHelper<T>(entity);
	}

	public void generateInsertSQLAndArgs() throws Exception {
		
		String insertSQL = "insert into {SQLTableName} ({SQLTableColumns}) values ({SQLTableColumnValueWildcards})";
		List<Object> insertArgs = new ArrayList<>();
		
		StringBuilder sqlTableColumns = new StringBuilder();
		StringBuilder sqlTableColumnValueWildcards = new StringBuilder();
		Class<? extends Object> clazz = this.entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
    	for (Field field: fields) {
			int fieldTypeModifiers = field.getType().getModifiers();
			String fieldTypeName = field.getType().getName();
			if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && (fieldTypeName.equals("java.math.BigDecimal") || fieldTypeName.equals("java.sql.Date") || fieldTypeName.equals("java.sql.Timestamp"))) {
    			sqlTableColumns.append(", ").append(EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName()));
    			sqlTableColumnValueWildcards.append(", ").append("?");
    			insertArgs.add(clazz.getDeclaredMethod(EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName(field.getName())).invoke(this.entity));
    		}
    	}
    	sqlTableColumns = sqlTableColumns.delete(0, 2);
    	sqlTableColumnValueWildcards = sqlTableColumnValueWildcards.delete(0, 2);
    	
    	insertSQL = insertSQL.replace("{SQLTableName}", EntityTableTransformUtils.fromEntityNameToTableName(clazz.getSimpleName()));
    	insertSQL = insertSQL.replace("{SQLTableColumns}", sqlTableColumns);
    	insertSQL = insertSQL.replace("{SQLTableColumnValueWildcards}", sqlTableColumnValueWildcards);
    	this.insertSQL = insertSQL;
    	this.insertArgs = insertArgs;
	}
	
	public void generateUpdateSQLAndArgs() throws Exception {
		
		String updateSQL = "update {SQLTableName} set {SQLTableColumnsAndValueWildcards} where id = ?";
		List<Object> updateArgs = new ArrayList<>();
		
		StringBuilder SQLTableColumnsAndValueWildcards = new StringBuilder();
		Class<? extends Object> clazz = this.entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
    	for (Field field: fields) {
			int fieldTypeModifiers = field.getType().getModifiers();
			String fieldTypeName = field.getType().getName();
			if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && (fieldTypeName.equals("java.math.BigDecimal") || fieldTypeName.equals("java.sql.Date") || fieldTypeName.equals("java.sql.Timestamp"))) {
    			if (!field.getName().equals("id")) {
    				SQLTableColumnsAndValueWildcards.append(", ").append(EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName())).append(" = ?");
        			updateArgs.add(clazz.getDeclaredMethod(EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName(field.getName())).invoke(this.entity));
    			}
    		}
    	}
    	SQLTableColumnsAndValueWildcards = SQLTableColumnsAndValueWildcards.delete(0, 2);
    	updateArgs.add(clazz.getDeclaredMethod(EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName("id")).invoke(this.entity));
    	
    	updateSQL = updateSQL.replace("{SQLTableName}", EntityTableTransformUtils.fromEntityNameToTableName(clazz.getSimpleName()));
    	updateSQL = updateSQL.replace("{SQLTableColumnsAndValueWildcards}", SQLTableColumnsAndValueWildcards);
    	this.updateSQL = updateSQL;
    	this.updateArgs = updateArgs;
	}

	public void generateUpdateSelectiveSQLAndArgs() throws Exception {

		String sql = "update {SQLTableName} set {SQLTableColumnsAndValueWildcards} where id = ?";
		List<Object> args = new ArrayList<>();

		StringBuilder SQLTableColumnsAndValueWildcards = new StringBuilder();
		Class<? extends Object> clazz = this.entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field: fields) {
			int fieldTypeModifiers = field.getType().getModifiers();
			String fieldTypeName = field.getType().getName();
			if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && (fieldTypeName.equals("java.math.BigDecimal") || fieldTypeName.equals("java.sql.Date") || fieldTypeName.equals("java.sql.Timestamp"))) {
				String fieldName = field.getName();
				Object fieldValue = clazz.getDeclaredMethod(EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName(fieldName)).invoke(this.entity);
				if (!fieldName.equals("id") && fieldValue != null) {
					SQLTableColumnsAndValueWildcards.append(", ").append(EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(fieldName)).append(" = ?");
					args.add(fieldValue);
				}
			}
		}
		SQLTableColumnsAndValueWildcards = SQLTableColumnsAndValueWildcards.delete(0, 2);
		args.add(clazz.getDeclaredMethod(EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName("id")).invoke(this.entity));

		sql = sql.replace("{SQLTableName}", EntityTableTransformUtils.fromEntityNameToTableName(clazz.getSimpleName()));
		sql = sql.replace("{SQLTableColumnsAndValueWildcards}", SQLTableColumnsAndValueWildcards);
		this.updateSelectiveSQL = sql;
		this.updateSelectiveArgs = args;
	}
	
	public String getInsertSQL() {
		if (this.insertSQL == null) {
			try {
				this.generateInsertSQLAndArgs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.insertSQL;
	}
	
	public List<Object> getInsertArgs() {
		if (this.insertArgs == null) {
			try {
				this.generateInsertSQLAndArgs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.insertArgs;
	}

	public String getUpdateSQL() {
		if (this.updateSQL == null) {
			try {
				this.generateUpdateSQLAndArgs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.updateSQL;
	}

	public List<Object> getUpdateArgs() {
		if (this.updateArgs == null) {
			try {
				this.generateUpdateSQLAndArgs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.updateArgs;
	}

	public String getUpdateSelectiveSQL() {
		if (this.updateSelectiveSQL == null) {
			try {
				this.generateUpdateSelectiveSQLAndArgs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.updateSelectiveSQL;
	}

	public List<Object> getUpdateSelectiveArgs() {
		if (this.updateSelectiveArgs == null) {
			try {
				this.generateUpdateSelectiveSQLAndArgs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.updateSelectiveArgs;
	}
}

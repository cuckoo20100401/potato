package org.potato.util.db;

import org.potato.util.StringUtils;

public class EntityTableTransformUtils {
	
	public static String fromTableNameToEntityName(String tableName) {
		return StringUtils.fromKebabCaseToPascalCase(tableName);
	}
	
	public static String fromTableColumnNameToEntityPropertyName(String tableColumnName) {
		String result = StringUtils.fromKebabCaseToPascalCase(tableColumnName);
		return StringUtils.makeFirstCharToLowerCase(result);
	}

	public static String fromEntityNameToTableName(String entityName) {
		return StringUtils.fromPascalCaseToKebabCase(entityName);
	}
	
	public static String fromEntityPropertyNameToTableColumnName(String entityPropertyName) {
		return StringUtils.fromPascalCaseToKebabCase(entityPropertyName);
	}
	
	public static String fromEntityPropertyNameToGetMethodName(String entityPropertyName) {
		return "get" + StringUtils.makeFirstCharToUpperCase(entityPropertyName);
	}
	
	public static String fromEntityPropertyNameToSetMethodName(String entityPropertyName) {
		return "set" + StringUtils.makeFirstCharToUpperCase(entityPropertyName);
	}
}

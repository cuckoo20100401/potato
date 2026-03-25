package org.potato.jdbc;

import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import org.potato.util.db.EntityTableTransformUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class BaseMapperProvider {

    private static final String[] allowedFieldTypeNames = new String[]{"java.math.BigDecimal", "java.sql.Date", "java.sql.Timestamp"};

    public String insert(Object entity, ProviderContext context) {

        Class<?> entityClass = entity.getClass();
        Field[] entityClassFields = entityClass.getDeclaredFields();
        String tableName = EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName());

        return new SQL() {{
            INSERT_INTO(tableName);
            for (Field field : entityClassFields) {
                int fieldTypeModifiers = field.getType().getModifiers();
                String fieldTypeName = field.getType().getName();
                if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && Arrays.asList(allowedFieldTypeNames).contains(fieldTypeName)) {
                    String column = EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName());
                    VALUES(column, "#{" + field.getName() + "}");
                }
            }
        }}.toString();
    }

    public String insertBatch(List<?> entities, ProviderContext context) {

        if (entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("The entities list inserted in batches cannot be empty");
        }

        Object firstEntity = entities.get(0);
        Class<?> entityClass = firstEntity.getClass();
        Field[] entityClassFields = entityClass.getDeclaredFields();
        String tableName = EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName());

        // build columns
        StringBuilder columns = new StringBuilder();
        for (Field field : entityClassFields) {
            int fieldTypeModifiers = field.getType().getModifiers();
            String fieldTypeName = field.getType().getName();
            if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && Arrays.asList(allowedFieldTypeNames).contains(fieldTypeName)) {
                String column = EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName());
                if (!columns.isEmpty()) {
                    columns.append(", ");
                }
                columns.append(column);
            }
        }

        // build values
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < entities.size(); i++) {
            if (!values.isEmpty()) {
                values.append(", ");
            }
            values.append("(");
            for (Field field : entityClassFields) {
                int fieldTypeModifiers = field.getType().getModifiers();
                String fieldTypeName = field.getType().getName();
                if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && Arrays.asList(allowedFieldTypeNames).contains(fieldTypeName)) {
                    if (!values.toString().endsWith("(")) {
                        values.append(", ");
                    }
                    values.append("#{list[").append(i).append("].").append(field.getName()).append("}");
                }
            }
            values.append(")");
        }

        return "INSERT INTO " + tableName + " (" + columns.toString() + ") VALUES " + values.toString();
    }
}

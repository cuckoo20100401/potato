package org.potato.jdbc.mapper;

import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import org.potato.util.db.EntityTableTransformUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    public String update(Object entity, ProviderContext context) {

        Class<?> entityClass = entity.getClass();
        Field[] entityClassFields = entityClass.getDeclaredFields();
        String tableName = EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName());

        return new SQL() {{
            UPDATE(tableName);
            for (Field field : entityClassFields) {
                int fieldTypeModifiers = field.getType().getModifiers();
                String fieldTypeName = field.getType().getName();
                if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && Arrays.asList(allowedFieldTypeNames).contains(fieldTypeName)) {
                    if (!field.getName().equals("id")) {
                        String column = EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName());
                        SET(column + " = #{" + field.getName() + "}");
                    }
                }
            }
            WHERE("id = #{id}");
        }}.toString();
    }

    public String updateSelective(Object entity, ProviderContext context) throws Exception {

        Class<?> entityClass = entity.getClass();
        Field[] entityClassFields = entityClass.getDeclaredFields();
        String tableName = EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName());

        return new SQL() {{
            UPDATE(tableName);
            for (Field field : entityClassFields) {
                int fieldTypeModifiers = field.getType().getModifiers();
                String fieldTypeName = field.getType().getName();
                if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && Arrays.asList(allowedFieldTypeNames).contains(fieldTypeName)) {
                    Object fieldValue = entityClass.getDeclaredMethod(EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName(field.getName())).invoke(entity);
                    if (!field.getName().equals("id") && fieldValue != null) {
                        String column = EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName());
                        SET(column + " = #{" + field.getName() + "}");
                    }
                }
            }
            WHERE("id = #{id}");
        }}.toString();
    }

    public String delete(Object id, ProviderContext context) {

        Class<?> entityClass = getEntityClass(context);
        String tableName = EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName());

        return new SQL() {{
            DELETE_FROM(tableName);
            WHERE("id = #{id}");
        }}.toString();
    }

    public String deleteBatch(Object[] ids, ProviderContext context) {

        Class<?> entityClass = getEntityClass(context);
        String tableName = EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName());

        StringBuilder values = new StringBuilder();
        return new SQL() {{
            DELETE_FROM(tableName);
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    values.append(", ");
                }
                values.append("#{array[").append(i).append("]}");
            }
            WHERE("id IN (" + values.toString() + ")");
        }}.toString();
    }

    private Class<?> getEntityClass(ProviderContext context) {
        for (Type genericInterface : context.getMapperType().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                Type rawType = parameterizedType.getRawType();
                if (rawType.equals(org.potato.jdbc.mapper.BaseMapper.class)) {
                    return (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
            }
        }
        throw new IllegalStateException("Unable to determine entity class from mapper type: " + context.getMapperType());
    }
}

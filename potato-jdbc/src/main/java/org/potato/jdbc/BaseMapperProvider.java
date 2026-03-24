package org.potato.jdbc;

import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import org.potato.util.db.EntityTableTransformUtils;

import java.lang.reflect.Field;

public class BaseMapperProvider {

    public String insert(Object entity, ProviderContext context) {

        Class<?> entityClass = entity.getClass();
        String tableName = EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName());

        return new SQL() {{
            INSERT_INTO(tableName);
            for (Field field : entityClass.getDeclaredFields()) {
                int fieldTypeModifiers = field.getType().getModifiers();
                String fieldTypeName = field.getType().getName();
                if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && (fieldTypeName.equals("java.math.BigDecimal") || fieldTypeName.equals("java.sql.Date") || fieldTypeName.equals("java.sql.Timestamp"))) {
                    String column = EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName());
                    VALUES(column, "#{" + field.getName() + "}");
                }
            }
        }}.toString();
    }
}

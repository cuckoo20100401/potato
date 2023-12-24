package org.potato.jdbc.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.jdbc.EntityPersistHelper;
import org.potato.jdbc.JdbcTemplateExt;
import org.potato.util.db.EntityTableTransformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class BaseDAOImpl<T> implements BaseDAO<T> {

    private static final Logger logger = LogManager.getLogger(BaseDAOImpl.class);

    protected Class entityClass;

    @Autowired
    protected JdbcTemplateExt jdbcTemplateExt;
    @Autowired
    protected ObjectMapper objectMapper;

    public BaseDAOImpl() {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        entityClass = (Class) parameterizedType.getActualTypeArguments()[0];
    }

    @Override
    public int insert(Object entity) {
        return jdbcTemplateExt.insert(EntityPersistHelper.build(entity));
    }

    @Override
    public int update(Object entity) {
        return jdbcTemplateExt.update(EntityPersistHelper.build(entity));
    }

    @Override
    public int updateSelective(T entity) {
        return jdbcTemplateExt.updateSelective(EntityPersistHelper.build(entity));
    }

    @Override
    public int delete(String id) {
        String sql = "delete from " + EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName()) + " where id = ?";
        logger.debug(sql);
        logger.debug(Arrays.asList(id));
        return jdbcTemplateExt.update(sql, id);
    }

    @Override
    public int delete(String[] ids) {
        int affectedRows = 0;
        for (String id: ids) {
            affectedRows += this.delete(id);
        }
        return affectedRows;
    }

    @Override
    public T findOne(String id) {

        String sql = "select {SQLTableColumnsAndAliases} from {SQLTableName} where id = ?";

        StringBuilder sqlTableColumnsAndAliases = new StringBuilder();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field: fields) {
            int fieldTypeModifiers = field.getType().getModifiers();
            String fieldTypeName = field.getType().getName();
            if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && (fieldTypeName.equals("java.math.BigDecimal") || fieldTypeName.equals("java.sql.Date") || fieldTypeName.equals("java.sql.Timestamp"))) {
                sqlTableColumnsAndAliases.append(", ").append(EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName())).append(" as \"").append(field.getName()).append("\"");
            }
        }
        sqlTableColumnsAndAliases = sqlTableColumnsAndAliases.delete(0, 2);

        sql = sql.replace("{SQLTableColumnsAndAliases}", sqlTableColumnsAndAliases);
        sql = sql.replace("{SQLTableName}", EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName()));
        logger.debug(sql);
        logger.debug(Arrays.asList(id));
        try {
            Map<String, Object> row = jdbcTemplateExt.queryForMap(sql, id);
            return (T) objectMapper.readValue(objectMapper.writeValueAsString(row), entityClass);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> findAll() {

        String sql = "select {SQLTableColumnsAndAliases} from {SQLTableName}";

        StringBuilder sqlTableColumnsAndAliases = new StringBuilder();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field: fields) {
            int fieldTypeModifiers = field.getType().getModifiers();
            String fieldTypeName = field.getType().getName();
            if (fieldTypeModifiers == 17 || fieldTypeModifiers == 1 && (fieldTypeName.equals("java.math.BigDecimal") || fieldTypeName.equals("java.sql.Date") || fieldTypeName.equals("java.sql.Timestamp"))) {
                sqlTableColumnsAndAliases.append(", ").append(EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName(field.getName())).append(" as \"").append(field.getName()).append("\"");
            }
        }
        sqlTableColumnsAndAliases = sqlTableColumnsAndAliases.delete(0, 2);

        sql = sql.replace("{SQLTableColumnsAndAliases}", sqlTableColumnsAndAliases);
        sql = sql.replace("{SQLTableName}", EntityTableTransformUtils.fromEntityNameToTableName(entityClass.getSimpleName()));
        logger.debug(sql);
        try {
            List<T> entities = new ArrayList<>();
            List<Map<String, Object>> rows = jdbcTemplateExt.queryForList(sql);
            for (Map<String, Object> row: rows) {
                entities.add((T) objectMapper.readValue(objectMapper.writeValueAsString(row), entityClass));
            }
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

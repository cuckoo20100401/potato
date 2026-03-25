package org.potato.jdbc.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.potato.jdbc.BaseMapperProvider;

import java.util.List;

public interface BaseMapper<T> {

    @InsertProvider(type = BaseMapperProvider.class, method = "insert")
    int insert(T entity);

    @InsertProvider(type = BaseMapperProvider.class, method = "insertBatch")
    int insertBatch(List<T> entities);

    int update(T entity);

    int updateSelective(T entity);

    int delete(String id);

    int delete(String[] ids);

    T findOne(String id);

    List<T> findAll();
}

package org.potato.jdbc.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.potato.jdbc.BaseMapperProvider;

import java.util.List;

public interface BaseMapper<T> {

    @InsertProvider(type = BaseMapperProvider.class, method = "insert")
    int insert(T entity);

    @InsertProvider(type = BaseMapperProvider.class, method = "insertBatch")
    int insertBatch(List<T> entities);

    @UpdateProvider(type = BaseMapperProvider.class, method = "update")
    int update(T entity);

    @UpdateProvider(type = BaseMapperProvider.class, method = "updateSelective")
    int updateSelective(T entity);

    @DeleteProvider(type = BaseMapperProvider.class, method = "delete")
    int delete(Object id);

    @DeleteProvider(type = BaseMapperProvider.class, method = "deleteBatch")
    int deleteBatch(Object[] ids);
}

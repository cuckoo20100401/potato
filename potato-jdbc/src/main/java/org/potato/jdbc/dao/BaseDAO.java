package org.potato.jdbc.dao;

import java.util.List;

public interface BaseDAO<T> {

    int insert(T entity);

    int update(T entity);

    int updateSelective(T entity);

    int delete(String id);

    int delete(String[] ids);

    T findOne(String id);

    List<T> findAll();
}

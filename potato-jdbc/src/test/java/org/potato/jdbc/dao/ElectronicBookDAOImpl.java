package org.potato.jdbc.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.potato.jdbc.DBUtils;
import org.potato.jdbc.entity.ElectronicBook;
import org.potato.util.web.PageInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ElectronicBookDAOImpl extends BaseDAOImpl<ElectronicBook> implements ElectronicBookDAO {

    public ElectronicBookDAOImpl() {
        super.jdbcTemplateExt = DBUtils.getJdbcTemplateExt();
        super.objectMapper = new ObjectMapper();
    }

    @Override
    public PageInfo<ElectronicBook> findList1() {
        String sql = "select id as \"id\", name as \"name\", author_name as \"authorName\", size as \"size\", price1 as \"price1\", price2 as \"price2\", create_date as \"createDate\", create_time as \"createTime\" from electronic_book";
        PageInfo<ElectronicBook> pageInfo = jdbcTemplateExt.findList(sql, null, 1, 3, new RowMapper<ElectronicBook>() {
            @Override
            public ElectronicBook mapRow(ResultSet rs, int rowNum) throws SQLException {
                ElectronicBook entity = new ElectronicBook();
                entity.setId(rs.getString("id"));
                entity.setName(rs.getString("name"));
                entity.setAuthorName(rs.getString("authorName"));
                entity.setSize(rs.getInt("size"));
                entity.setPrice1(rs.getBigDecimal("price1"));
                entity.setPrice2(rs.getDouble("price2"));
                entity.setCreateDate(rs.getDate("createDate"));
                entity.setCreateTime(rs.getTimestamp("createTime"));
                return entity;
            }
        });
        return pageInfo;
    }

    @Override
    public PageInfo<Map<String, Object>> findList2() {
        String sql = "select id as \"id\", name as \"name\", author_name as \"authorName\", size as \"size\", price1 as \"price1\", price2 as \"price2\", create_date as \"createDate\", create_time as \"createTime\" from electronic_book";
        return jdbcTemplateExt.findList(sql, null, 1, 5);
    }
}

package org.potato.jdbc.dao;

import org.potato.jdbc.entity.ElectronicBook;
import org.potato.util.web.PageInfo;

import java.util.Map;

public interface ElectronicBookDAO extends BaseDAO<ElectronicBook> {

    PageInfo<ElectronicBook> findList1();

    PageInfo<Map<String, Object>> findList2();
}

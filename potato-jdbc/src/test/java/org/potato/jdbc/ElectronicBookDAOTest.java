package org.potato.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.potato.jdbc.dao.ElectronicBookDAO;
import org.potato.jdbc.dao.ElectronicBookDAOImpl;
import org.potato.jdbc.entity.ElectronicBook;
import org.potato.util.web.PageInfo;

public class ElectronicBookDAOTest {

	ElectronicBookDAO electronicBookDAO = new ElectronicBookDAOImpl();

	public static void main(String[] args) {
		ElectronicBookDAOTest test = new ElectronicBookDAOTest();
//		test.insert();
//		test.update();
//		test.updateSelective();
//		test.delete();
//		test.findOne();
//		test.findAll();
//		test.findList1();
		test.findList2();
	}
	
	public void insert() {
		ElectronicBook electronicBook = new ElectronicBook();
		electronicBook.setId("eb000010");
		electronicBook.setName("天龙八部");
		electronicBook.setAuthorName("桥峰");
		electronicBook.setSize(1024);
		electronicBook.setPrice1(new BigDecimal(104.52));
		electronicBook.setPrice2(204.52);
		electronicBook.setCreateDate(Date.valueOf(LocalDate.now()));
		electronicBook.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
		int affectedRowCount = electronicBookDAO.insert(electronicBook);
		System.out.println("affectedRowCount: " + affectedRowCount);
	}
	
	public void update() {
		ElectronicBook dbElectronicBook = electronicBookDAO.findOne("eb000005");
		if (dbElectronicBook != null) {
			dbElectronicBook.setAuthorName("潘金莲");
			int affectedRowCount = electronicBookDAO.update(dbElectronicBook);
			System.out.println("affectedRowCount: " + affectedRowCount);
		}
	}

	public void updateSelective() {
		ElectronicBook dbElectronicBook = electronicBookDAO.findOne("eb000004");
		if (dbElectronicBook != null) {
			dbElectronicBook.setAuthorName("孙悟空");
			int affectedRowCount = electronicBookDAO.updateSelective(dbElectronicBook);
			System.out.println("affectedRowCount: " + affectedRowCount);
		}
	}

	public void delete() {
		System.out.println("affectedRowCount: " + electronicBookDAO.delete("eb000001"));
		System.out.println("affectedRowCount: " + electronicBookDAO.delete(new String[]{"eb000002", "eb000003"}));
	}

	public void findOne() {
		System.out.println("-->" + electronicBookDAO.findOne("eb000010"));
		System.out.println("-->" + electronicBookDAO.findOne("eb111111"));
	}

	public void findAll() {
		List<ElectronicBook> electronicBooks = electronicBookDAO.findAll();
		for (ElectronicBook electronicBook: electronicBooks) {
			try {
				System.out.println(new ObjectMapper().writeValueAsString(electronicBook));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void findList1() {
		PageInfo<ElectronicBook> pageInfo = electronicBookDAO.findList1();
		for (ElectronicBook electronicBook: pageInfo.getList()) {
			try {
				System.out.println(new ObjectMapper().writeValueAsString(electronicBook));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void findList2() {
		PageInfo<Map<String, Object>> pageInfo = electronicBookDAO.findList2();
		for (Map<String, Object> row: pageInfo.getList()) {
			try {
				System.out.println(new ObjectMapper().writeValueAsString(row));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

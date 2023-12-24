package org.potato.jdbc.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class ElectronicBook implements Serializable {

	private static final long serialVersionUID = -5895255385159467731L;
	
	private String id;
	private String name;
	private String authorName;
	private Integer size;
	private BigDecimal price1;
	private Double price2;
	private Date createDate;
	private Timestamp createTime;
	private Category category;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public BigDecimal getPrice1() {
		return price1;
	}

	public void setPrice1(BigDecimal price1) {
		this.price1 = price1;
	}

	public Double getPrice2() {
		return price2;
	}

	public void setPrice2(Double price2) {
		this.price2 = price2;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}

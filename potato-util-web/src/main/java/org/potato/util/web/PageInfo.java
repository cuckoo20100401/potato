package org.potato.util.web;

import java.io.Serializable;
import java.util.List;

public class PageInfo<T> implements Serializable {
	
	private static final long serialVersionUID = 5965246754215069653L;
	
	private int pageNum;			//当前页
	private int pageSize;			//每页显示记录数
	private List<T> list;			//结果集
	private long total;				//总记录数
	
	public PageInfo() {}

	public PageInfo(int pageNum, int pageSize, List<T> list, long total) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.list = list;
		this.total = total;
	}

	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
}
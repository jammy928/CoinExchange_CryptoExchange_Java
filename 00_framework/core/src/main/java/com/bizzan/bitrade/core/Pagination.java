/**
 * 
 */
package com.bizzan.bitrade.core;

import java.util.List;


/**
 * @author yanqizheng
 * 存储分页信息
 */
public class Pagination {
	private int currentPage;
	private int pageSize;
	private int totalRecord;
	private List<?> items;
	
	public Pagination(){
		
	}
	
	public Pagination(int currPage, int pageSize){
		this.currentPage = currPage;
		this.pageSize = pageSize;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public List<?> getItems() {
		return items;
	}
	public void setItems(List<?> items) {
		this.items = items;
	}
}

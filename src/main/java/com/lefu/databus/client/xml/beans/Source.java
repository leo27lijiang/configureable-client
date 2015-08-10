package com.lefu.databus.client.xml.beans;

import java.util.List;

public class Source {
	/**
	 * Mysql类型标志
	 */
	public static final int MYSQL = 1;
	/**
	 * Oracle类型标志
	 */
	public static final int ORACLE = 2;
	
	private String name;
	private Integer id;
	private String table;
	private Integer db;
	private Boolean logEnable = true;
	private List<Field> fields;
	
	public Source() {
		
	}
	
	public Source(String name, Integer id, String table, int db, Boolean logEnable, List<Field> fields) {
		this.name = name;
		this.id = id;
		this.table = table;
		this.db = db;
		this.logEnable = logEnable;
		this.fields = fields;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public Integer getDb() {
		return db;
	}
	public Boolean getLogEnable() {
		return logEnable;
	}
	public void setLogEnable(Boolean logEnable) {
		this.logEnable = logEnable;
	}
	public void setDb(Integer db) {
		this.db = db;
	}
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("name=" + this.name + ",");
		sb.append("id=" + this.id + ",");
		sb.append("table=" + this.table + ",");
		sb.append("db=" + this.db + ",");
		sb.append("logEnable=" + this.logEnable + ",");
		sb.append("fields=" + this.fields.toString());
		sb.append("}");
		return sb.toString();
	}
}

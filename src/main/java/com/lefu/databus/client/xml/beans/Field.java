package com.lefu.databus.client.xml.beans;

public class Field {
	private String name;
	private String type;
	private Boolean primaryKey;
	
	public Field() {
		
	}
	
	public Field(String name, String type, Boolean primaryKey) {
		this.name = name;
		this.type = type;
		this.primaryKey = primaryKey;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("name=" + this.name + ",");
		sb.append("type=" + this.type + ",");
		sb.append("primaryKey=" + this.primaryKey);
		sb.append("}");
		return sb.toString();
	}
}

package com.lefu.databus.client;

import com.lefu.databus.client.xml.beans.Field;

public class Pair {
	private Field field;
	private Object value;
	
	public Pair() {
		
	}
	
	public Pair(Field field, Object value) {
		this.field = field;
		this.value = value;
	}
	
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		if (field == null || field.getName() == null) {
			return 0;
		}
		return field.getName().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) {
			return false;
		}
		Pair target = (Pair) o;
		if (this.field == null && target.field == null) {
			return true;
		}
		if (this.field != null && target.field != null) {
			if (this.field.getName() != null && target.field.getName() != null) {
				return this.field.getName().equals(target.field.getName());
			}
		}
		return false;
	}
	
}

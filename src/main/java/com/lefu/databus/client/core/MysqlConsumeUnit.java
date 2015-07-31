package com.lefu.databus.client.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericRecord;

import com.lefu.databus.client.Pair;
import com.lefu.databus.client.util.VariableUtil;
import com.lefu.databus.client.xml.beans.Field;

public class MysqlConsumeUnit extends AbstractConsumeUnit {
	/**
	 * %1 table
	 * %2 fields
	 * %3 wildcards
	 */
	public static final String UPSERT_TEMPLATE = "REPLACE INTO %1$s (%2$s) VALUES(%3$s)";
	
	private String upsertSql = null;
	
	public MysqlConsumeUnit() {
		
	}
	
	@Override
	protected String getUpsertSql() {
		if (this.upsertSql == null) {
			StringBuffer fields = new StringBuffer();
			StringBuffer values = new StringBuffer();
			for (Field field : this.source.getFields()) {
				fields.append(field.getName());
				fields.append(",");
				values.append("?,");
			}
			fields.deleteCharAt(fields.length() - 1);
			values.deleteCharAt(values.length() - 1);
			this.upsertSql = String.format(UPSERT_TEMPLATE, this.source.getTable(), fields.toString(), values.toString());
		}
		return this.upsertSql;
	}

	@Override
	protected List<Pair> getParams(GenericRecord record) {
		List<Pair> pairs = new ArrayList<Pair>();
		for (Field field : this.source.getFields()) {
			Object value = VariableUtil.getRecordValue(field, record);
			pairs.add(new Pair(field, value));
		}
		return pairs;
	}

}

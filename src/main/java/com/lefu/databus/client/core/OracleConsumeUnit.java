package com.lefu.databus.client.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;

import com.lefu.databus.client.Pair;
import com.lefu.databus.client.util.VariableUtil;
import com.lefu.databus.client.xml.beans.Field;

/**
 * Oracle Merge
 * @author jiang.li
 *
 */
public class OracleConsumeUnit extends AbstractConsumeUnit {
	/**
	 * %1 table
	 * %2 primary key
	 * %3 merge_update
	 * %4 merge_insert
	 */
	public final String UPSERT_TEMPLATE = "MERGE INTO %1$s USING DUAL ON (%2$s = ?) WHEN MATCHED THEN %3$s WHEN NOT MATCHED THEN %4$s";
	
	private String upsertSql = null;
	
	public OracleConsumeUnit() {
		
	}
	
	@Override
	protected String getUpsertSql() {
		if (this.upsertSql == null) {
			StringBuffer mergeUpdate = new StringBuffer();
			StringBuffer mergeInsert = new StringBuffer();
			StringBuffer mergeInsertValues = new StringBuffer();
			mergeUpdate.append("UPDATE SET ");
			mergeInsert.append("INSERT(");
			mergeInsertValues.append(") VALUES(");
			for (Field field : this.source.getFields()) {
				if (field.getPrimaryKey()) {
					continue;
				}
				mergeUpdate.append(field.getName());
				mergeUpdate.append("=?,");
			}
			for (Field field : this.source.getFields()) {
				mergeInsert.append(field.getName());
				mergeInsert.append(",");
				mergeInsertValues.append("?,");
			}
			mergeUpdate.deleteCharAt(mergeUpdate.length() - 1);
			mergeInsert.deleteCharAt(mergeInsert.length() - 1);
			mergeInsertValues.deleteCharAt(mergeInsertValues.length() - 1);
			mergeInsert.append(mergeInsertValues);
			mergeInsert.append(")");
			this.upsertSql = String.format(UPSERT_TEMPLATE, this.source.getTable(), this.primaryKey.getName(), mergeUpdate.toString(), mergeInsert.toString());
			LOG.info("Generate oracle merge sql {}", this.upsertSql);
		}
		return this.upsertSql;
	}
	
	@Override
	protected List<Pair> getParams(GenericRecord record) {
		Map<String, Object> rawValues = new HashMap<String, Object>();
		for (Field field : this.source.getFields()) {
			Object value = VariableUtil.getRecordValue(field, record);
			rawValues.put(field.getName(), value);
		}
		List<Pair> pairs = new ArrayList<Pair>();
		pairs.add(new Pair(this.primaryKey, rawValues.get(this.primaryKey.getName())));// %2 primary key
		for (Field field : this.source.getFields()) {//merge_update
			if (field.getPrimaryKey()) {
				continue;
			}
			pairs.add(new Pair(field, rawValues.get(field.getName())));
		}
		for (Field field : this.source.getFields()) {//merge_insert
			pairs.add(new Pair(field, rawValues.get(field.getName())));
		}
		return pairs;
	}

}

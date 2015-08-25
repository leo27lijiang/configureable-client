package com.lefu.databus.client.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lefu.databus.client.ConsumeUnit;
import com.lefu.databus.client.Pair;
import com.lefu.databus.client.util.VariableUtil;
import com.lefu.databus.client.xml.beans.Field;
import com.lefu.databus.client.xml.beans.Source;
import com.linkedin.databus.core.DbusEvent;
import com.linkedin.databus.core.DbusOpcode;

public abstract class AbstractConsumeUnit implements ConsumeUnit {
	/**
	 * %1 table
	 * %2 primary key
	 */
	public static final String DELETE_TEMPLATE = "DELETE FROM %1$s WHERE %2$s = ?";
	
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractConsumeUnit.class);
	protected DataSource dataSource;
	protected Source source;
	protected String deleteSql = null;
	protected Field primaryKey;
	
	public AbstractConsumeUnit() {
		
	}
	
	public AbstractConsumeUnit(DataSource dataSource, Source source) {
		setDataSource(dataSource);
		setSource(source);
	}
	
	@Override
	public DataSource getDataSource() {
		return this.dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public Source getSource() {
		return this.source;
	}
	
	public void setSource(Source source) {
		this.source = source;
		for (Field field : source.getFields()) {
			if (field.getPrimaryKey()) {
				this.primaryKey = field;
				break;
			}
		}
		if (this.primaryKey == null) {
			throw new RuntimeException("Primary key not found");
		}
		if (source.getFields().size() < 2) {
			throw new RuntimeException("Source must contain at least 2 fields");
		}
		this.deleteSql = String.format(DELETE_TEMPLATE, this.source.getTable(), this.primaryKey.getName());
		LOG.info("Generate delete sql {}", this.deleteSql);
	}

	@Override
	public Integer getSourceId() {
		return this.source.getId();
	}

	@Override
	public void execute(DbusEvent event, Map<String, Object> rawValues) throws Exception {
		if (event.getOpcode().equals(DbusOpcode.UPSERT)) {
			executeUpsert(rawValues);
		} else {
			executeDelete(rawValues);
		}
	}
	
	private void executeUpsert(Map<String, Object> rawValues) throws Exception {
		Connection conn = this.dataSource.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(getUpsertSql());
			List<Pair> params = getParams(rawValues);
			for (int i = 0; i < params.size(); i++) {
				int position = i + 1;
				Pair pair = params.get(i);
				if (pair.getValue() == null) {
					pstmt.setNull(position, VariableUtil.getSqlType(pair.getField()));
				} else {
					pstmt.setObject(position, pair.getValue());
				}
			}
			int result = pstmt.executeUpdate();
			if (this.source.getLogEnable()) {
				LOG.info("Upsert affected rows {} in source {} with PK[{}={}]", result, this.source.getName(), primaryKey.getName(),
						rawValues.get(primaryKey.getName()));
			}
		} catch(Exception e) {
			LOG.error("Executing upsert error in {} PK[{}={}]", this.source.getName(), primaryKey.getName(),
					rawValues.get(primaryKey.getName()));
			throw e;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			conn.close();
		}
	}
	
	private void executeDelete(Map<String, Object> rawValues) throws Exception {
		Connection conn = this.dataSource.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(this.deleteSql);
			Object value = rawValues.get(primaryKey.getName());
			pstmt.setObject(1, value);
			int result = pstmt.executeUpdate();
			if (this.source.getLogEnable()) {
				LOG.info("Delete affected rows {} in source {} with PK[{}={}]", result, this.source.getName(), primaryKey.getName(), value);
			}
		} catch (Exception e) {
			LOG.error("Executing delete error in {} PK[{}={}]", this.source.getName(), primaryKey.getName(),
					rawValues.get(primaryKey.getName()));
			throw e;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			conn.close();
		}
	}
	
	/**
	 * 生产 UPSERT SQL语句
	 * @return
	 */
	protected abstract String getUpsertSql();
	
	/**
	 * 根据 {@link #getUpsertSql()} 获取排序好的实际参数
	 * @param rawValues
	 * @return
	 */
	protected abstract List<Pair> getParams(Map<String, Object> rawValues);

}

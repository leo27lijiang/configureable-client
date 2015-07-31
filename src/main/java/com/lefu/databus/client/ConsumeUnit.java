package com.lefu.databus.client;

import javax.sql.DataSource;

import org.apache.avro.generic.GenericRecord;

import com.lefu.databus.client.xml.beans.Source;
import com.linkedin.databus.core.DbusEvent;

/**
 * 消费单元
 * @author jiang.li
 *
 */
public interface ConsumeUnit {
	/**
	 * 注入数据源
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource);
	/**
	 * 注入源配置
	 * @param source
	 */
	public void setSource(Source source);
	/**
	 * 数据ID
	 * @return
	 */
	public Integer getSourceId();
	/**
	 * 执行导入逻辑
	 * @param event
	 * @param record
	 * @throws Exception
	 */
	public void execute(DbusEvent event, GenericRecord record) throws Exception;
}

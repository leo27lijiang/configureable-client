package com.lefu.databus.client;

import java.util.Map;

import javax.sql.DataSource;

import com.lefu.databus.client.xml.beans.Source;
import com.linkedin.databus.core.DbusEvent;

/**
 * 消费单元
 * @author jiang.li
 *
 */
public interface ConsumeUnit {
	/**
	 * 
	 * @return
	 */
	public DataSource getDataSource();
	/**
	 * 
	 * @return
	 */
	public Source getSource();
	/**
	 * 数据ID
	 * @return
	 */
	public Integer getSourceId();
	/**
	 * 执行导入逻辑
	 * @param event
	 * @param rawValues 根据field描述转换为java对象类型
	 * @throws Exception
	 */
	public void execute(DbusEvent event, Map<String, Object> rawValues) throws Exception;
}

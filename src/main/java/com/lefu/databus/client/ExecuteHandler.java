package com.lefu.databus.client;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.lefu.databus.client.xml.beans.Source;
import com.linkedin.databus.core.DbusEvent;

/**
 * 
 * @author jiang.li
 *
 */
public interface ExecuteHandler {
	/**
	 * 检查是否处理此数据源
	 * @param sourceId
	 * @return
	 */
	public Boolean isRelatedSource(Integer sourceId);
	/**
	 * 初始化
	 * @param sources
	 * @param dataSource
	 */
	public void init(List<Source> sources, DataSource dataSource);
	/**
	 * 执行过滤
	 * @param event
	 * @param rawValues
	 * @return <code>true</code> 过滤此次操作 <code>false</code> 不过滤
	 */
	public boolean doFilter(DbusEvent event, Map<String, Object> rawValues);
	/**
	 * 事件入库之前
	 * @param event
	 * @param rawValues
	 */
	public void before(DbusEvent event, Map<String, Object> rawValues);
	/**
	 * 事件入库之后，执行异常则不会执行此方法
	 * @param event
	 * @param rawValues
	 */
	public void after(DbusEvent event, Map<String, Object> rawValues);
	/**
	 * 
	 */
	public void destroy();
}

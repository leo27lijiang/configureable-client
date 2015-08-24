package com.lefu.databus.client.util;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;

import com.lefu.databus.client.xml.beans.Field;

/**
 * 
 * @author jiang.li
 *
 */
public class VariableUtil {
	/**
	 * 表达式
	 */
	public static final String regex = "(\\$\\{[a-zA-Z_0-9\\. \\-:]+\\})";
	/**
	 * 正则表达式
	 */
	private static Pattern pattern = Pattern.compile(regex);
	
	/**
	 * 检测给定字符串中是否含有表达式
	 * @param expression
	 * @return
	 */
	public static boolean isVariable(String expression){
		Matcher m = pattern.matcher(expression);
		return m.find();
	}
	
	/**
	 * 获取表达式中的变量字符串
	 * @param expression
	 * @return
	 */
	public static List<String> getVariables(String expression){
		List<String> list = new ArrayList<String>();
		Matcher m = pattern.matcher(expression);
		while(m.find()){
			list.add(getVariableText(m.group()));
		}
		return list;
	}
	
	/**
	 * 替换字符串中的变量为指定的通配符
	 * @param source
	 * @param wildcard
	 * @return
	 */
	public static String replaceVariables(String source, String wildcard) {
		return source.replaceAll(regex, wildcard);
	}
	
	/**
	 * 获取变量内的文本.
	 * @param var
	 * @return
	 */
	public static String getVariableText(String var){
		if(var == null || var.length() < 3){
			throw new RuntimeException("Variable text error");
		}
		return var.substring(2, var.length() - 1).trim();
	}
	
	/**
	 * 根据field定义获取record的值，不匹配时进行转换
	 * @param field
	 * @param record
	 * @return
	 */
	public static Object getRecordValue(Field field, GenericRecord record) {
		String type = field.getType();
		Object value = field.getAlias() != null ? record.get(field.getAlias()) : record.get(field.getName());
		if (value == null) {
			return null;
		}
		if (type.equals(String.class.getName())) {
			Utf8 utf8 = (Utf8) value;
			return utf8.toString();
		} else if (type.equals(Long.class.getName()) || type.equals("long")) {
			return (Long) value;
		} else if (type.equals(Integer.class.getName()) || type.equals("int")) {
			return (Integer) value;
		} else if (type.equals(Double.class.getName()) || type.equals("double")) {
			return (value instanceof Double) ? value : Double.parseDouble(value.toString());
		} else if (type.equals(Float.class.getName()) || type.equals("float")) {
			return (value instanceof Float) ? value : Float.parseFloat(value.toString());
		} else if (type.equals(Boolean.class.getName()) || type.equals("boolean")) {
			return (value instanceof Boolean) ? value : Boolean.parseBoolean(value.toString());
		} else if (type.equals(java.util.Date.class.getName())) {
			Long time = (Long) value;
			return new java.util.Date(time);
		} else if (type.equals(java.sql.Date.class.getName())) {
			Long time = (Long) value;
			return new java.sql.Date(time);
		} else if (type.equals(Byte.class.getName()) || type.equals("byte")) {
			return (value instanceof Byte) ? value : Byte.parseByte(value.toString());
		} else if (type.equals(BigDecimal.class.getName())) {
			return new BigDecimal(value.toString());
		} else if (type.equals(Character.class.getName()) || type.equals("char")) {
			return (Character) value;
		}
		throw new RuntimeException("Not supported type " + field.getType());
	}
	
	/**
	 * 获取field对应的SqlType
	 * @param field
	 * @return
	 */
	public static int getSqlType(Field field) {
		String type = field.getType();
		if (type.equals(String.class.getName())) {
			return Types.VARCHAR;
		} else if (type.equals(Long.class.getName()) || type.equals("long")) {
			return Types.BIGINT;
		} else if (type.equals(Integer.class.getName()) || type.equals("int")) {
			return Types.INTEGER;
		} else if (type.equals(Double.class.getName()) || type.equals("double")) {
			return Types.DOUBLE;
		} else if (type.equals(Float.class.getName()) || type.equals("float")) {
			return Types.FLOAT;
		} else if (type.equals(Boolean.class.getName()) || type.equals("boolean")) {
			return Types.BOOLEAN;
		} else if (type.equals(java.sql.Date.class.getName()) || type.equals(java.util.Date.class.getName())) {
			return Types.DATE;
		} else if (type.equals(Byte.class.getName()) || type.equals("byte")) {
			return Types.BIT;
		} else if (type.equals(BigDecimal.class.getName())) {
			return Types.DECIMAL;
		} else if (type.equals(Character.class.getName()) || type.equals("char")) {
			return Types.CHAR;
		}
		throw new RuntimeException("Not supported type " + field.getType());
	}
	
}

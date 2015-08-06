package com.lefu.databus.client.xml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lefu.databus.client.xml.beans.Field;
import com.lefu.databus.client.xml.beans.Source;

/**
 * 
 * @author jiang.li
 *
 */
public abstract class XmlParser {
	static Logger log = LoggerFactory.getLogger(XmlParser.class);
	
	/**
	 * 解析配置文件，默认读取 ClassPath 下的 configure.xml 文件
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public static List<Source> parser() throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(XmlParser.class.getClassLoader().getResourceAsStream("configure.xml"));
		List<Source> sources = new ArrayList<Source>();
		Element root = doc.getRootElement();
		for (Element sourceNode : (List<Element>) root.elements("source")) {
			Attribute idAttr = sourceNode.attribute("id");
			if (idAttr == null) {
				throw new RuntimeException("<source> tag not contain id attribute");
			}
			Integer sourceId = Integer.parseInt(idAttr.getText());
			Attribute nameAttr = sourceNode.attribute("name");
			String sourceName = (nameAttr != null ? nameAttr.getText() : null);
			Attribute tableAttr = sourceNode.attribute("table");
			if (tableAttr == null) {
				throw new RuntimeException("<source> tag not contain table attribute");
			}
			String table = tableAttr.getText();
			Attribute dbAttr = sourceNode.attribute("db");
			if (dbAttr == null) {
				throw new RuntimeException("<source> tag not contain db attribute");
			}
			int db = Source.ORACLE;
			if ("mysql".equals(dbAttr.getText())) {
				db = Source.MYSQL;
			}
			Element fieldsNode = sourceNode.element("fields");
			if (fieldsNode == null) {
				throw new RuntimeException("No <fields> found in <source> " + sourceId);
			}
			List<Field> fields = new ArrayList<Field>();
			for (Element fieldNode : (List<Element>) fieldsNode.elements("field")) {
				Attribute fieldNameAttr = fieldNode.attribute("name");
				if (fieldNameAttr == null) {
					throw new RuntimeException("Parser field error " + fieldNode.asXML());
				}
				String fieldName = fieldNameAttr.getText();
				Attribute fieldTypeAttr = fieldNode.attribute("type");
				String type = (fieldTypeAttr != null ? fieldTypeAttr.getText() : String.class.getName());
				Attribute fieldAliasAttr = fieldNode.attribute("alias");
				String alias = (fieldAliasAttr != null ? fieldAliasAttr.getText() : null);
				Attribute fieldPrimaryKeyAttr = fieldNode.attribute("primaryKey");
				Boolean primaryKey = (fieldPrimaryKeyAttr != null ? new Boolean(fieldPrimaryKeyAttr.getText()) : new Boolean(false));
				fields.add(new Field(fieldName, type, alias, primaryKey));
			}
			sources.add(new Source(sourceName, sourceId, table, db, fields));
		}
		log.info("Parser configure.xml finish.");
		log.info(sources.toString());
		return sources;
	}
	
}

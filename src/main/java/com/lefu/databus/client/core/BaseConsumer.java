package com.lefu.databus.client.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.GenericData.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lefu.databus.client.ConsumeUnit;
import com.lefu.databus.client.ExecuteHandler;
import com.lefu.databus.client.util.VariableUtil;
import com.lefu.databus.client.xml.beans.Field;
import com.linkedin.databus.client.consumer.AbstractDatabusCombinedConsumer;
import com.linkedin.databus.client.pub.ConsumerCallbackResult;
import com.linkedin.databus.client.pub.DbusEventDecoder;
import com.linkedin.databus.core.DbusEvent;

public class BaseConsumer extends AbstractDatabusCombinedConsumer {
	private static final Logger log = LoggerFactory.getLogger(BaseConsumer.class);
	private static String ERROR_DIR;//Save error state for monitor system
	private static File errorDir;
	private Map<Integer,ConsumeUnit> dispatcher;
	private ExecuteHandler executeHandler;
	
	static {
		ERROR_DIR = System.getProperty("configurable.error.dir", "var");
		errorDir = new File(ERROR_DIR);
		log.info("Setting error state directory {}", errorDir.getAbsolutePath());
	}
	
	public BaseConsumer() {
		
	}
	
	@Override
	public ConsumerCallbackResult onDataEvent(DbusEvent event,
			DbusEventDecoder eventDecoder) {
		return processEvent(event, eventDecoder);
	}

	@Override
	public ConsumerCallbackResult onBootstrapEvent(DbusEvent event,
			DbusEventDecoder eventDecoder) {
		return processEvent(event, eventDecoder);
	}
	
	private ConsumerCallbackResult processEvent(DbusEvent event,
			DbusEventDecoder eventDecoder) {
		try {
			Record decodedEvent = (Record)eventDecoder.getGenericRecord(event, null);
			ConsumeUnit unit = this.dispatcher.get(event.getSourceId());
			if (unit == null) {
				log.warn("SourceID {} consumer not found", event.getSourceId());
				return ConsumerCallbackResult.ERROR_FATAL;
			}
			Map<String, Object> rawValues = new HashMap<String, Object>();
			for (org.apache.avro.Schema.Field f : decodedEvent.getSchema().getFields()) {// Get all the values
				boolean isMatched = false;
				for (Field field : unit.getSource().getFields()) {
					if (f.name().equals(field.getName()) || f.name().equals(field.getAlias())) {
						Object value = VariableUtil.getRecordValue(field, decodedEvent);
						rawValues.put(field.getName(), value);
						isMatched = true;
						break;
					}
				}
				if (!isMatched) {
					rawValues.put(f.name(), decodedEvent.get(f.name()));
				}
			}
			boolean isRelated = false;
			boolean isFiltered = false;
			if (this.executeHandler != null && this.executeHandler.isRelatedSource(event.getSourceId())) {
				isRelated = true;
			}
			if (isRelated) {
				try {
					isFiltered = this.executeHandler.doFilter(event, rawValues);
				} catch (Throwable f) {
					f.printStackTrace();
				}
			}
			if (isRelated) {
				try {
					this.executeHandler.before(event, rawValues);
				} catch (Throwable before) {
					before.printStackTrace();
				}
			}
			if (isFiltered) {
				log.info("SourceID {} was filtered", event.getSourceId());
			} else {
				unit.execute(event, rawValues);
				if (isRelated) {
					try {
						this.executeHandler.after(event, rawValues);
					} catch (Throwable after) {
						after.printStackTrace();
					}
				}
			}
			return ConsumerCallbackResult.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Create an empty file flag an error happened
		String fileName = errorDir.getAbsolutePath() + File.separator + String.valueOf(event.getSourceId());
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				boolean created = file.createNewFile();
				if(!created) log.error("Save error state failed, can not create state file {}", fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ConsumerCallbackResult.ERROR;
	}

	public void setDispatcher(Map<Integer, ConsumeUnit> dispatcher) {
		this.dispatcher = dispatcher;
	}

	public void setExecuteHandler(ExecuteHandler executeHandler) {
		this.executeHandler = executeHandler;
	}
}

package com.lefu.databus.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.lefu.databus.client.core.BaseConsumer;
import com.lefu.databus.client.core.MysqlConsumeUnit;
import com.lefu.databus.client.core.OracleConsumeUnit;
import com.lefu.databus.client.xml.XmlParser;
import com.lefu.databus.client.xml.beans.Source;
import com.linkedin.databus.client.DatabusHttpClientImpl;
import com.linkedin.databus.client.DatabusHttpClientImpl.StaticConfig;
import com.linkedin.databus.client.pub.ServerInfo.ServerInfoBuilder;
import com.linkedin.databus.core.util.ConfigLoader;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConfigurableClient {
	private static ComboPooledDataSource dataSource;
	private DatabusHttpClientImpl client;
	
	public ConfigurableClient() {
		
	}
	
	public void start() {
		try {
			List<Source> sources = XmlParser.parser();
			Map<Integer, ConsumeUnit> dispatcher = new HashMap<Integer, ConsumeUnit>();
			for (Source s : sources) {
				ConsumeUnit unit = null;
				if (s.getDb() == Source.ORACLE) {
					unit = new OracleConsumeUnit();
				} else {
					unit = new MysqlConsumeUnit();
				}
				unit.setDataSource(getDataSource());
				unit.setSource(s);
				dispatcher.put(s.getId(),  unit);
			}
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("client.properties"));
			DatabusHttpClientImpl.Config configBuilder = new DatabusHttpClientImpl.Config();
			ConfigLoader<DatabusHttpClientImpl.StaticConfig> staticConfigLoader = new ConfigLoader<StaticConfig>("databus.client.", configBuilder);
			DatabusHttpClientImpl.StaticConfig staticConfig = staticConfigLoader.loadConfig(props);
			client = new DatabusHttpClientImpl(staticConfig);
			List<String> schemas = new ArrayList<String>();
			for (ServerInfoBuilder builder : staticConfig.getRuntime().getRelays().values()) {
				String[] schema = builder.getSources().split("[" + ServerInfoBuilder.SOURCE_SEPARATOR + "]");
				for (String s:schema) {
					schemas.add(s);
				}
			}
			String[] rawSchemas = new String[schemas.size()];
			schemas.toArray(rawSchemas);
			BaseConsumer consumer = new BaseConsumer();
			consumer.setDispatcher(dispatcher);
		    client.registerDatabusStreamListener(consumer, null, rawSchemas);
		    client.registerDatabusBootstrapListener(consumer, null, rawSchemas);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (client != null) {
			Runtime.getRuntime().addShutdownHook(new ShutdownHook(dataSource, client));
			client.startAndBlock();
		}
	}
	
	public DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = new ComboPooledDataSource();
			try {
				Properties prop = new Properties();
				prop.load(this.getClass().getClassLoader().getResourceAsStream("db.properties"));
				dataSource.setDriverClass(prop.getProperty(prop.getProperty("jdbc.driverClassName")));
				dataSource.setJdbcUrl(prop.getProperty("jdbc.url"));
				dataSource.setUser(prop.getProperty("jdbc.username"));
				dataSource.setPassword(prop.getProperty("jdbc.password"));
				dataSource.setMinPoolSize(Integer.parseInt(prop.getProperty("jdbc.pool.min.size", "1")));
				dataSource.setMaxPoolSize(Integer.parseInt(prop.getProperty("jdbc.pool.max.size", "3")));
				dataSource.setInitialPoolSize(1);
				dataSource.setMaxIdleTime(1800);
				dataSource.setIdleConnectionTestPeriod(1800);
				dataSource.setAcquireIncrement(1);
				dataSource.setMaxStatements(0);
				dataSource.setBreakAfterAcquireFailure(true);
				dataSource.setTestConnectionOnCheckin(false);
				dataSource.setTestConnectionOnCheckout(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dataSource;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ConfigurableClient().start();
	}

}

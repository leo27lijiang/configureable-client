package com.lefu.databus.client;

import com.linkedin.databus.client.DatabusHttpClientImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ShutdownHook extends Thread {
	private final ComboPooledDataSource dataSource;
	private final DatabusHttpClientImpl client;
	
	public ShutdownHook(ComboPooledDataSource dataSource, DatabusHttpClientImpl client) {
		this.dataSource = dataSource;
		this.client = client;
	}
	
	@Override
	public void run() {
		if (this.client != null) {
			this.client.shutdown();
		}
		if (this.dataSource != null) {
			this.dataSource.close();
		}
	}
	
}

package com.lefu.databus.client;

import com.linkedin.databus.client.DatabusHttpClientImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ShutdownHook extends Thread {
	private final ComboPooledDataSource dataSource;
	private final DatabusHttpClientImpl client;
	private final ExecuteHandler executeHandler;
	
	public ShutdownHook(ComboPooledDataSource dataSource, DatabusHttpClientImpl client, ExecuteHandler executeHandler) {
		this.dataSource = dataSource;
		this.client = client;
		this.executeHandler = executeHandler;
	}
	
	@Override
	public void run() {
		if (this.client != null) {
			try {
				this.client.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (this.executeHandler != null) {
			try {
				this.executeHandler.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (this.dataSource != null) {
			this.dataSource.close();
		}
	}
	
}

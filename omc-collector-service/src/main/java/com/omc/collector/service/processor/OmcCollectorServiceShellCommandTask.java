package com.omc.collector.service.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class OmcCollectorServiceShellCommandTask implements Runnable {

	private BlockingQueue<String> requestQueue;
	private String shellCommand;
	private ConfigurableApplicationContext context;

	private final Log logger = LogFactory.getLog(OmcCollectorServiceShellCommandTask.class);

	public OmcCollectorServiceShellCommandTask(BlockingQueue<String> requestQueue, String shellCommand, ConfigurableApplicationContext context) {
		this.requestQueue = requestQueue;
		this.shellCommand = shellCommand;
		this.context = context;
	}

	@Override
	public void run() {
		logger.debug("Starting Shell Command worker thread");
		try {
			Process p = Runtime.getRuntime().exec(shellCommand);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				// do something interesting with the line
				this.requestQueue.put(line);
				logger.debug("Get message from shell command: " + line);
			}
			if (p.exitValue() != 0) {
				throw new IOException("Command " + shellCommand + " exited with " + p.exitValue());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			context.close();
		}
	}
}

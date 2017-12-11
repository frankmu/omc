package com.omc.collector.service.processor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OmcCollectorServiceShellCommandTask implements Runnable {

	private BlockingQueue<String> requestQueue;
	private String shellCommand;

	private final Log logger = LogFactory.getLog(OmcCollectorServiceShellCommandTask.class);

	public OmcCollectorServiceShellCommandTask(BlockingQueue<String> requestQueue, String shellCommand) {
		this.requestQueue = requestQueue;
		this.shellCommand = shellCommand;
	}

	@Override
	public void run() {
		logger.debug("Starting Shell Command worker thread");
		try {
			Process p = Runtime.getRuntime().exec(shellCommand);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = reader.readLine();
				if (line == null) {
					// wait until there is more of the file for us to read
					Thread.sleep(1000);
				} else {
					// do something interesting with the line
					this.requestQueue.put(line);
					logger.debug("Get message from shell command: " + line);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}

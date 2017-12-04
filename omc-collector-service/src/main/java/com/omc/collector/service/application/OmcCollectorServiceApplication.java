package com.omc.collector.service.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@ComponentScan({"com.omc"})
public class OmcCollectorServiceApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(OmcCollectorServiceApplication.class)
		.logStartupInfo(false)
		.run(args);
	}
}
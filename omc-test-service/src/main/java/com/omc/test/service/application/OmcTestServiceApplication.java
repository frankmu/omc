package com.omc.test.service.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@ComponentScan({"com.omc"})
public class OmcTestServiceApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(OmcTestServiceApplication.class)
		.logStartupInfo(false)
		.run(args);
	}
}
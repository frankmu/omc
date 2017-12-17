package com.omc.preprocess.service.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@ComponentScan({"com.omc.preprocess"})
public class OmcPreprocessServiceApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(OmcPreprocessServiceApplication.class)
		.logStartupInfo(false)
		.run(args);
	}
}
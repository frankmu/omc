package com.omc.service.common.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan({"com.omc"})
public class OmcCommonServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(OmcCommonServiceApplication.class, args);
	}
}
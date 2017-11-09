package com.omc.test.service.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan({"com.omc"})
public class OmcTestServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(OmcTestServiceApplication.class, args);
	}
}
package com.exalca.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.exalca")
public class ExalcaDsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExalcaDsApplication.class, args);
	}

}

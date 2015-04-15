package com.jgermaine.fyp.rest.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;


@ComponentScan(basePackages = "com.jgermaine.fyp.rest")
@EnableAutoConfiguration
public class WebApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(WebApplication.class, args);
		Logger LOGGER = LogManager
				.getLogger(WebApplication.class.getName());

        LOGGER.info("Printing Pre-Loaded Spring Boot Beans:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            LOGGER.info(beanName);
        }
	}
}

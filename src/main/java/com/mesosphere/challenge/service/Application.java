package com.mesosphere.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.mesosphere.challenge.service.config.StorageServiceConfig;

/**
 * 
 * @author john
 *
 *         This is the main Spring Boot application that launches the challenge
 *         storage service
 *
 */
@SpringBootApplication
public class Application {

	/*
	 * This @Autowired declaration tells Spring to automatically populate this
	 * field with a maching bean found in the Spring config.
	 */
	@Autowired
	StorageServiceConfig config;

	/**
	 * 
	 * Main storage service entry point. Used to launch the API.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

		};
	}

}
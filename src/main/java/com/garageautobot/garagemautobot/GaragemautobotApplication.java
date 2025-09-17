package com.garageautobot.garagemautobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.garageautobot.garagemautobot.entities")
public class GaragemautobotApplication {

	public static void main(String[] args) {
		SpringApplication.run(GaragemautobotApplication.class, args);
	}

}

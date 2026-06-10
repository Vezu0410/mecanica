package com.garageautobot.garagemautobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // habilita o @Scheduled do backup automático
public class GaragemautobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaragemautobotApplication.class, args);
    }
}
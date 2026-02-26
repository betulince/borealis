package com.betuince.borealis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BorealisApplication {

	public static void main(String[] args) {
		SpringApplication.run(BorealisApplication.class, args);
	}

}

package com.homestay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class HomestayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomestayApplication.class, args);
	}

}

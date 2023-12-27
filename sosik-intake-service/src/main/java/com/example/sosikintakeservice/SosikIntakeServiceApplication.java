package com.example.sosikintakeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class SosikIntakeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SosikIntakeServiceApplication.class, args);
	}

}

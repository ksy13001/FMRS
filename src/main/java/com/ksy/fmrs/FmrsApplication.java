package com.ksy.fmrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class FmrsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FmrsApplication.class, args);
	}

}

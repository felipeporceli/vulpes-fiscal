package com.vulpesfiscal.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VulpesFiscalApplication {

	public static void main(String[] args) {
		SpringApplication.run(VulpesFiscalApplication.class, args);
	}

}

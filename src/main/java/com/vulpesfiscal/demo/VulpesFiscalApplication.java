package com.vulpesfiscal.demo;

import com.vulpesfiscal.demo.configuration.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(RsaKeyProperties.class)
public class VulpesFiscalApplication {

	public static void main(String[] args) {
		SpringApplication.run(VulpesFiscalApplication.class, args);
	}

}

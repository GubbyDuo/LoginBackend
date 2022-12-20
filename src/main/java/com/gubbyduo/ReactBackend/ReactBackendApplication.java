package com.gubbyduo.ReactBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.gubbyduo.ReactBackend.*")
@ComponentScan(basePackages = { "com.gubbyduo.ReactBackend.*" })
@EntityScan("com.gubbyduo.ReactBackend.*")
@SpringBootApplication
public class ReactBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactBackendApplication.class, args);
	}
}

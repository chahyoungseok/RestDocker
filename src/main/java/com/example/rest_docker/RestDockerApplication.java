package com.example.rest_docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class RestDockerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestDockerApplication.class, args);
	}

}

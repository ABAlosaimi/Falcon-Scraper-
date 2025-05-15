package com.proxy.falcon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FalconApplication {

	public static void main(String[] args) {
		SpringApplication.run(FalconApplication.class, args);
	}

}

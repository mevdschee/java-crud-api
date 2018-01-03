package com.tqdev.crudapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.tqdev.crudapi.memory.CrudApiServiceImpl;
import com.tqdev.crudapi.service.CrudApiService;

@SpringBootApplication(scanBasePackages = { "com.tqdev.crudapi" })
public class CrudApiApp {

	public static void main(String[] args) {
		SpringApplication.run(CrudApiApp.class, args);
	}

	@Bean
	public CrudApiService crudApiService() {
		return new CrudApiServiceImpl();
	}
}

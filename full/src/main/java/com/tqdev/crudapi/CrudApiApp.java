package com.tqdev.crudapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.tqdev.crudapi.memory.CrudApiServiceImpl;
import com.tqdev.crudapi.service.CrudApiService;

@SpringBootApplication(scanBasePackages = { "com.tqdev.crudapi" })
@PropertySource("classpath:application.yml")
public class CrudApiApp {

	public static void main(String[] args) {
		SpringApplication.run(CrudApiApp.class, args);
	}

	@Value("${crud.driver.name}")
	String crudDriverName;

	@Bean
	public CrudApiService crudApiService() {
		CrudApiService result;
		switch (crudDriverName) {
		case "memory":
			result = new CrudApiServiceImpl();
			break;
		default:
			result = null;
		}
		return result;
	}
}

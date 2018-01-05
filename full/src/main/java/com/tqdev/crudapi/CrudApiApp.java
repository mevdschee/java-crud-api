package com.tqdev.crudapi;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.memory.CrudApiServiceImpl;
import com.tqdev.crudapi.service.CrudApiService;

@SpringBootApplication(scanBasePackages = { "com.tqdev.crudapi" })
@PropertySource("classpath:application.yml")
public class CrudApiApp {

	public static void main(String[] args) {
		SpringApplication.run(CrudApiApp.class, args);
	}

	@Value("${driver.name}")
	String crudDriverName;

	@Value("${driver.url}")
	String crudDriverUrl;

	@Bean
	public CrudApiService crudApiService() throws JsonParseException, JsonMappingException, IOException {
		CrudApiService result;
		switch (crudDriverName) {
		case "memory":
			result = new CrudApiServiceImpl(crudDriverUrl);
			break;
		default:
			result = null;
		}
		return result;
	}
}

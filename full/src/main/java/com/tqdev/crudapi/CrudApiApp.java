package com.tqdev.crudapi;

import java.io.IOException;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.JooqCrudApiService;
import com.tqdev.crudapi.service.MemoryCrudApiService;

@SpringBootApplication(scanBasePackages = { "com.tqdev.crudapi" })
@PropertySource("classpath:application.yml")
public class CrudApiApp {

	public static void main(String[] args) {
		SpringApplication.run(CrudApiApp.class, args);
	}

	@Value("${driver.name}")
	String crudDriverName;

	@Bean
	@Autowired
	public CrudApiService crudApiService(DSLContext dsl) throws JsonParseException, JsonMappingException, IOException {
		CrudApiService result;
		switch (crudDriverName) {
		case "memory":
			// see:
			// https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html
			result = new MemoryCrudApiService();
			result.initialize("columns.json", "records.json");
			break;
		case "jooq":
			System.getProperties().setProperty("org.jooq.no-logo", "true");
			result = new JooqCrudApiService(dsl);
			result.initialize("columns.json", "records.json");
			break;
		default:
			result = null;
		}
		return result;
	}
}

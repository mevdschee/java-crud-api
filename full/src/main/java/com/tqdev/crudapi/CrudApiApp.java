package com.tqdev.crudapi;

import java.io.IOException;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.JooqCrudApiService;
import com.tqdev.crudapi.service.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.service.record.DatabaseRecordsException;

@SpringBootApplication(scanBasePackages = { "com.tqdev.crudapi" })
@PropertySource("classpath:application.yml")
public class CrudApiApp {

	public static void main(String[] args) {
		SpringApplication.run(CrudApiApp.class, args);
	}

	@Bean
	@Autowired
	@Transactional
	public CrudApiService crudApiService(DSLContext dsl) throws JsonParseException, JsonMappingException, IOException,
			DatabaseDefinitionException, DatabaseRecordsException {
		CrudApiService result;
		System.getProperties().setProperty("org.jooq.no-logo", "true");
		result = new JooqCrudApiService(dsl);
		result.initialize("columns.json", "records.json");
		return result;
	}
}

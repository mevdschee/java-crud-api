package com.tqdev.crudapi;

import java.io.IOException;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.core.CrudApiService;
import com.tqdev.crudapi.core.JooqCrudApiService;
import com.tqdev.crudapi.core.record.DatabaseRecordsException;
import com.tqdev.crudapi.meta.CrudMetaService;
import com.tqdev.crudapi.meta.JooqCrudMetaService;
import com.tqdev.crudapi.meta.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.spatial.SpatialDSL;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication(scanBasePackages = { "com.tqdev.crudapi" })
@PropertySource("classpath:application.yml")
public class CrudApiApp {

	public static void main(String[] args) {
		SpringApplication.run(CrudApiApp.class, args);
	}

	@Bean
	@Autowired
	public CrudMetaService crudMetaService(DSLContext dsl) throws JsonParseException, JsonMappingException, IOException,
			DatabaseDefinitionException, DatabaseRecordsException {
		CrudMetaService result;
		SpatialDSL.registerDataTypes(dsl);
		result = new JooqCrudMetaService(dsl);
		result.initialize("columns.json");
		return result;
	}

	@Bean
	@Autowired
	public CrudApiService crudApiService(DSLContext dsl, CrudMetaService meta) throws JsonParseException,
			JsonMappingException, IOException, DatabaseDefinitionException, DatabaseRecordsException {
		CrudApiService result;
		result = new JooqCrudApiService(dsl, meta);
		result.initialize("records.json");
		return result;
	}

}

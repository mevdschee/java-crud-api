package com.tqdev.crudapi;

import com.tqdev.crudapi.api.CrudApiService;
import com.tqdev.crudapi.api.JooqCrudApiService;
import com.tqdev.crudapi.api.record.DatabaseRecordsException;
import com.tqdev.crudapi.api.spatial.SpatialDSL;
import com.tqdev.crudapi.meta.CrudMetaService;
import com.tqdev.crudapi.meta.JooqCrudMetaService;
import com.tqdev.crudapi.meta.definition.DatabaseDefinitionException;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = { "com.tqdev.crudapi" })
@PropertySource("classpath:application.yml")
public class CrudApiApp {

	public static void main(String[] args) {
		SpringApplication.run(CrudApiApp.class, args);
	}

	@Bean
	@Autowired
	public CrudMetaService crudMetaService(DSLContext dsl) throws IOException,
			DatabaseDefinitionException, DatabaseRecordsException {
		CrudMetaService result;
		SpatialDSL.registerDataTypes(dsl);
		result = new JooqCrudMetaService(dsl);
		result.initialize("columns.json", "openapi.json");
		return result;
	}

	@Bean
	@Autowired
	public CrudApiService crudApiService(DSLContext dsl, CrudMetaService meta) throws
			IOException, DatabaseDefinitionException, DatabaseRecordsException {
		CrudApiService result;
		result = new JooqCrudApiService(dsl, meta);
		result.initialize("records.json");
		return result;
	}

}

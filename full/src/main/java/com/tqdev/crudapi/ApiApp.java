package com.tqdev.crudapi;

import com.tqdev.crudapi.column.JooqColumnService;
import com.tqdev.crudapi.column.ColumnService;
import com.tqdev.crudapi.column.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.controller.Responder;
import com.tqdev.crudapi.openapi.JooqOpenApiService;
import com.tqdev.crudapi.openapi.OpenApiService;
import com.tqdev.crudapi.record.RecordService;
import com.tqdev.crudapi.record.container.DatabaseRecordsException;
import com.tqdev.crudapi.record.JooqRecordService;
import com.tqdev.crudapi.record.spatial.SpatialDSL;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = { "com.tqdev.crudapi" })
@PropertySource("classpath:application.yml")
public class ApiApp {

	public static void main(String[] args) {
		SpringApplication.run(ApiApp.class, args);
	}

	@Bean
	@Autowired
	public Responder responder() {
		return new Responder();
	}

	@Bean
	@Autowired
	public OpenApiService openApiService(DSLContext dsl, ColumnService columns) throws IOException {
		OpenApiService result;
		result = new JooqOpenApiService(dsl, columns);
		result.initialize("openapi.json");
		return result;
	}

	@Bean
	@Autowired
	public ColumnService metaService(DSLContext dsl) throws IOException,
			DatabaseDefinitionException, DatabaseRecordsException {
		ColumnService result;
		SpatialDSL.registerDataTypes(dsl);
		result = new JooqColumnService(dsl);
		result.initialize("columns.json");
		return result;
	}

	@Bean
	@Autowired
	public RecordService dataService(DSLContext dsl, ColumnService columns) throws
			IOException, DatabaseDefinitionException, DatabaseRecordsException {
		RecordService result;
		result = new JooqRecordService(dsl, columns);
		result.initialize("records.json");
		return result;
	}

}

package com.tqdev.crudapi;

import com.tqdev.crudapi.data.DataService;
import com.tqdev.crudapi.data.JooqDataService;
import com.tqdev.crudapi.data.record.DatabaseRecordsException;
import com.tqdev.crudapi.data.spatial.SpatialDSL;
import com.tqdev.crudapi.meta.MetaService;
import com.tqdev.crudapi.meta.JooqMetaService;
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
public class ApiApp {

	public static void main(String[] args) {
		SpringApplication.run(ApiApp.class, args);
	}

	@Bean
	@Autowired
	public MetaService metaService(DSLContext dsl) throws IOException,
			DatabaseDefinitionException, DatabaseRecordsException {
		MetaService result;
		SpatialDSL.registerDataTypes(dsl);
		result = new JooqMetaService(dsl);
		result.initialize("columns.json", "openapi.json");
		return result;
	}

	@Bean
	@Autowired
	public DataService dataService(DSLContext dsl, MetaService meta) throws
			IOException, DatabaseDefinitionException, DatabaseRecordsException {
		DataService result;
		result = new JooqDataService(dsl, meta);
		result.initialize("records.json");
		return result;
	}

}

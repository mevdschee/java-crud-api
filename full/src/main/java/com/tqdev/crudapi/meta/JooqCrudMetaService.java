package com.tqdev.crudapi.meta;

import java.io.IOException;

import org.jooq.DSLContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.core.record.DatabaseRecordsException;
import com.tqdev.crudapi.meta.definition.DatabaseDefinition;
import com.tqdev.crudapi.meta.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.meta.openapi.OpenApiDefinition;
import com.tqdev.crudapi.meta.reflection.DatabaseReflection;

public class JooqCrudMetaService implements CrudMetaService {

	protected DSLContext dsl;

	protected DatabaseReflection tables;

	protected OpenApiDefinition baseOpenApiDefinition;

	public JooqCrudMetaService(DSLContext dsl) {
		this.dsl = dsl;
		this.tables = new DatabaseReflection(dsl);
	}

	@Override
	public DatabaseReflection getDatabaseReflection() {
		return tables;
	}

	@Override
	public DatabaseDefinition getDatabaseDefinition() {
		return new DatabaseDefinition(tables);
	}

	@Override
	public OpenApiDefinition getOpenApiDefinition() {
		OpenApiDefinition copy = new OpenApiDefinition(baseOpenApiDefinition);
		copy.inject(getDatabaseDefinition());
		return copy;
	}

	@Override
	public void initialize(String columnsFilename, String openApiFilename) throws JsonParseException,
			JsonMappingException, IOException, DatabaseDefinitionException, DatabaseRecordsException {
		DatabaseDefinition.fromFile(columnsFilename).create(dsl);
		tables.update();
		baseOpenApiDefinition = OpenApiDefinition.fromFile(openApiFilename);
	}

}

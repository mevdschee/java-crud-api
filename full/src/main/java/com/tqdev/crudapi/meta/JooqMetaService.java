package com.tqdev.crudapi.meta;

import com.tqdev.crudapi.meta.definition.DatabaseDefinition;
import com.tqdev.crudapi.meta.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.meta.openapi.OpenApiDefinition;
import com.tqdev.crudapi.meta.reflection.DatabaseReflection;
import org.jooq.DSLContext;

import java.io.IOException;

public class JooqMetaService implements MetaService {

	private DSLContext dsl;

	private DatabaseReflection tables;

	private OpenApiDefinition baseOpenApiDefinition;

	public JooqMetaService(DSLContext dsl) {
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
	public void initialize(String columnsFilename, String openApiFilename) throws
			IOException, DatabaseDefinitionException {
		DatabaseDefinition.fromFile(columnsFilename).create(dsl);
		tables.update();
		baseOpenApiDefinition = OpenApiDefinition.fromFile(openApiFilename);
	}

}

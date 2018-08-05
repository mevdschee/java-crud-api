package com.tqdev.crudapi.column;

import com.tqdev.crudapi.column.definition.DatabaseDefinition;
import com.tqdev.crudapi.column.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.column.reflection.DatabaseReflection;
import com.tqdev.crudapi.column.reflection.ReflectedTable;
import com.tqdev.crudapi.openapi.OpenApiDefinition;

import org.jooq.DSLContext;

import java.io.IOException;

public class JooqColumnService implements ColumnService {

	private DSLContext dsl;

	private DatabaseReflection tables;

	private OpenApiDefinition baseOpenApiDefinition;

	public JooqColumnService(DSLContext dsl) {
		this.dsl = dsl;
		this.tables = new DatabaseReflection(dsl);
	}

	@Override
	public DatabaseReflection getDatabase() {
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

	@Override
	public boolean hasTable(String tableName) {
		return this.tables.exists(tableName);
	}

	@Override
	public ReflectedTable getTable(String tableName) {
		return this.tables.get(tableName);
	}

}

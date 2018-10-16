package com.tqdev.crudapi.column;

import com.tqdev.crudapi.column.definition.DatabaseDefinition;
import com.tqdev.crudapi.column.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.column.reflection.DatabaseReflection;
import com.tqdev.crudapi.column.reflection.ReflectedTable;

import org.jooq.DSLContext;

import java.io.IOException;

public class JooqColumnService implements ColumnService {

	private DSLContext dsl;

	private DatabaseReflection reflection;

	public JooqColumnService(DSLContext dsl) {
		this.dsl = dsl;
		this.reflection = new DatabaseReflection(dsl);
	}

	@Override
	public DatabaseReflection getDatabaseReflection() {
		return reflection;
	}

	@Override
	public DatabaseDefinition getDatabaseDefinition() {
		return new DatabaseDefinition(reflection);
	}

	@Override
	public void initialize(String columnsFilename) throws
			IOException, DatabaseDefinitionException {
		DatabaseDefinition.fromFile(columnsFilename).create(dsl);
		reflection.update();
	}

	@Override
	public boolean hasTable(String tableName) {
		return this.reflection.hasTable(tableName);
	}

	@Override
	public ReflectedTable getTable(String tableName) {
		return this.reflection.getTable(tableName);
	}

}

package com.tqdev.crudapi.meta;

import java.io.IOException;

import org.jooq.DSLContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.core.record.DatabaseRecordsException;
import com.tqdev.crudapi.meta.definition.DatabaseDefinition;
import com.tqdev.crudapi.meta.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.meta.reflection.DatabaseReflection;
import com.tqdev.crudapi.spatial.SpatialDSL;

public class JooqCrudMetaService implements CrudMetaService {

	protected DSLContext dsl;

	protected DatabaseReflection tables;

	public JooqCrudMetaService(DSLContext dsl) {
		this.dsl = dsl;
		this.tables = new DatabaseReflection();
		SpatialDSL.registerDataTypes(dsl);
	}

	@Override
	public DatabaseDefinition getDatabaseDefinition() {
		return DatabaseDefinition.fromValue(tables);
	}

	@Override
	public void initialize(String columnsFilename) throws JsonParseException, JsonMappingException, IOException,
			DatabaseDefinitionException, DatabaseRecordsException {
		DatabaseDefinition.fromFile(columnsFilename).create(dsl);

	}

	@Override
	public DatabaseReflection getDatabaseReflection() {
		return tables;
	}

}

package com.tqdev.crudapi.column;

import java.io.IOException;

import com.tqdev.crudapi.column.definition.DatabaseDefinition;
import com.tqdev.crudapi.column.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.column.reflection.DatabaseReflection;
import com.tqdev.crudapi.column.reflection.ReflectedTable;
import com.tqdev.crudapi.data.record.DatabaseRecordsException;
import com.tqdev.crudapi.openapi.OpenApiDefinition;

public interface MetaService {

	// meta

	DatabaseReflection getDatabase();

	DatabaseDefinition getDatabaseDefinition();

	OpenApiDefinition getOpenApiDefinition();

	// initialization

	void initialize(String columnsFilename, String openApiFilename)
			throws IOException, DatabaseDefinitionException, DatabaseRecordsException;

	boolean hasTable(String tableName);

	ReflectedTable getTable(String tableName);
}

package com.tqdev.crudapi.meta;

import java.io.IOException;

import com.tqdev.crudapi.data.record.DatabaseRecordsException;
import com.tqdev.crudapi.meta.definition.DatabaseDefinition;
import com.tqdev.crudapi.meta.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.meta.openapi.OpenApiDefinition;
import com.tqdev.crudapi.meta.reflection.DatabaseReflection;

public interface MetaService {

	// meta

	DatabaseReflection getDatabaseReflection();

	DatabaseDefinition getDatabaseDefinition();

	OpenApiDefinition getOpenApiDefinition();

	// initialization

	void initialize(String columnsFilename, String openApiFilename)
			throws IOException, DatabaseDefinitionException, DatabaseRecordsException;

}

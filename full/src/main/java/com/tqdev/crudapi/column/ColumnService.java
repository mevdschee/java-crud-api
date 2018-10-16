package com.tqdev.crudapi.column;

import java.io.IOException;

import com.tqdev.crudapi.column.definition.DatabaseDefinition;
import com.tqdev.crudapi.column.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.column.reflection.DatabaseReflection;
import com.tqdev.crudapi.column.reflection.ReflectedTable;
import com.tqdev.crudapi.record.container.DatabaseRecordsException;

public interface ColumnService {

	// meta

	DatabaseReflection getDatabaseReflection();

	DatabaseDefinition getDatabaseDefinition();

	// initialization

	void initialize(String columnsFilename)
			throws IOException, DatabaseDefinitionException, DatabaseRecordsException;

	boolean hasTable(String tableName);

	ReflectedTable getTable(String tableName);
}

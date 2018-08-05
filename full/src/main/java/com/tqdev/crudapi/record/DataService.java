package com.tqdev.crudapi.record;

import com.tqdev.crudapi.column.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.data.record.DatabaseRecords;
import com.tqdev.crudapi.data.record.DatabaseRecordsException;
import com.tqdev.crudapi.data.record.Record;
import com.tqdev.crudapi.data.record.document.ListDocument;

import java.io.IOException;

public interface DataService {

	// crud

	boolean exists(String table);

	String create(String table, Record record, Params params);

	Record read(String table, String id, Params params);

	int update(String table, String id, Record record, Params params);

	int increment(String table, String id, Record record, Params params);

	int delete(String table, String id, Params params);

	ListDocument list(String table, Params params);

	// meta

	void update();

	DatabaseRecords getDatabaseRecords();

	// initialization

	void initialize(String recordsFilename) throws IOException,
			DatabaseDefinitionException, DatabaseRecordsException;

}

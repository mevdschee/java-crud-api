package com.tqdev.crudapi.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.record.DatabaseRecords;
import com.tqdev.crudapi.service.record.ListResponse;
import com.tqdev.crudapi.service.record.Record;

public interface CrudApiService {

	// crud

	String create(String table, Record record, Params params);

	Record read(String table, String id, Params params);

	Integer update(String table, String id, Record record, Params params);

	Integer delete(String table, String id, Params params);

	ListResponse list(String table, Params params);

	// meta

	boolean updateDefinition();

	DatabaseDefinition getDatabaseDefinition();

	DatabaseRecords getDatabaseRecords();

	// initialization

	void initialize(String string, String string2) throws JsonParseException, JsonMappingException, IOException;
}

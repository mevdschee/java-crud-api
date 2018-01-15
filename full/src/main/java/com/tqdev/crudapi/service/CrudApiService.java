package com.tqdev.crudapi.service;

public interface CrudApiService {

	boolean updateDefinition();

	// crud

	String create(String table, Record record, Params params);

	Record read(String table, String id, Params params);

	Integer update(String table, String id, Record record, Params params);

	Integer delete(String table, String id, Params params);

	ListResponse list(String table, Params params);

	// meta

	DatabaseDefinition getDatabaseDefinition();

	DatabaseRecords getDatabaseRecords();
}

package com.tqdev.crudapi.service;

public interface CrudApiService {

	boolean dropTable(String table);

	boolean createTable(String table, TableDefinition tableDefinition);

	String create(String table, Record record);

	Record read(String table, String id);

	Integer update(String table, String id, Record record);

	Integer delete(String table, String id);

	ListResponse list(String table);
}

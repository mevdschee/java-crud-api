package com.tqdev.crudapi.service;

public interface CrudApiService {

	boolean updateDefinition();

	String create(String table, Record record);

	Record read(String table, String id, Params params);

	Integer update(String table, String id, Record record);

	Integer delete(String table, String id);

	ListResponse list(String table, Params params);
}

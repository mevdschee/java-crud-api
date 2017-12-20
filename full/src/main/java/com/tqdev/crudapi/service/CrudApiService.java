package com.tqdev.crudapi.service;

public interface CrudApiService {

	String create(String entity, Record record);

	Record read(String entity, String id);

	Integer update(String entity, String id, Record record);

	Integer delete(String entity, String id);

	ListResponse list(String entity);
}

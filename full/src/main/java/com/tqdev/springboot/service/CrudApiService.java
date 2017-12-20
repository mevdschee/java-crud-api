package com.tqdev.springboot.service;

public interface CrudApiService {

	String create(String entity, Object user);

	Object read(String entity, String id);

	Integer update(String entity, String id, Object user);

	Integer delete(String entity, String id);

	ListResponse list(String entity);
}

package com.tqdev.springboot.service;

public interface DataApiService {

	String create(String entity, Object user);

	Object read(String entity, String id);

	int update(String entity, String id, Object user);

	int delete(String entity, String id);

	ListResponse list(String entity);
}

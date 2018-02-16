package com.tqdev.crudapi.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.core.CrudApiService;
import com.tqdev.crudapi.core.ErrorCode;
import com.tqdev.crudapi.core.Params;
import com.tqdev.crudapi.core.record.ListResponse;
import com.tqdev.crudapi.core.record.Record;

@RestController
@RequestMapping("/data")
public class CrudApiController extends BaseController {

	public static final Logger logger = LoggerFactory.getLogger(CrudApiController.class);

	@Autowired
	CrudApiService service;

	@RequestMapping(value = "/{table}", method = RequestMethod.GET)
	public ResponseEntity<?> list(@PathVariable("table") String table,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Listing table with name {} and parameters {}", table, params);
		if (!service.exists(table)) {
			return error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		ListResponse response = service.list(table, new Params(params));
		if (response == null) {
			return error(ErrorCode.CANNOT_LIST_TABLE, table);
		}
		return success(response);
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> read(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Reading record from {} with id {} and parameters {}", table, id, params);
		if (!service.exists(table)) {
			return error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		if (id.indexOf(',') >= 0) {
			ArrayList<Object> result = new ArrayList<>();
			for (String s : id.split(",")) {
				result.add(service.read(table, s, new Params(params)));
			}
			return success(result);
		} else {
			Object response = service.read(table, id, new Params(params));
			if (response == null) {
				return error(ErrorCode.RECORD_NOT_FOUND, id);
			}
			return success(response);
		}
	}

	@RequestMapping(value = "/{table}", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public ResponseEntity<?> create(@PathVariable("table") String table,
			@RequestBody MultiValueMap<String, String> record,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		ObjectMapper mapper = new ObjectMapper();
		Object pojo = mapper.convertValue(record.toSingleValueMap(), Object.class);
		return create(table, pojo, params);
	}

	@RequestMapping(value = "/{table}", method = RequestMethod.POST, headers = "Content-Type=application/json")
	public ResponseEntity<?> create(@PathVariable("table") String table, @RequestBody Object record,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Creating record in {} with properties {}", table, record);
		if (!service.exists(table)) {
			return error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		if (record instanceof ArrayList<?>) {
			ArrayList<Object> result = new ArrayList<>();
			for (Object o : (ArrayList<?>) record) {
				result.add(service.create(table, Record.valueOf(o), new Params(params)));
			}
			return success(result);
		} else {
			String response = service.create(table, Record.valueOf(record), new Params(params));
			if (response == null) {
				return error(ErrorCode.CANNOT_CREATE_RECORD, record.toString());
			}
			return success(response);
		}
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.PUT, headers = "Content-Type=application/x-www-form-urlencoded")
	public ResponseEntity<?> update(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestBody MultiValueMap<String, String> record,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		ObjectMapper mapper = new ObjectMapper();
		Object pojo = mapper.convertValue(record.toSingleValueMap(), Object.class);
		return update(table, id, pojo, params);
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.PUT, headers = "Content-Type=application/json")
	public ResponseEntity<?> update(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestBody Object record, @RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Updating record in {} with id {} and properties {}", table, id, record);
		if (!service.exists(table)) {
			return error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		if (id.indexOf(',') >= 0 && record instanceof ArrayList<?>) {
			ArrayList<Object> result = new ArrayList<>();
			String[] ids = id.split(",");
			ArrayList<?> records = new ArrayList<>();
			if (ids.length != records.size()) {
				return error(ErrorCode.ARGUMENT_COUNT_MISMATCH, id);
			}
			for (int i = 0; i < ids.length; i++) {
				result.add(service.update(table, ids[i], Record.valueOf(records.get(i)), new Params(params)));
			}
			return success(result);
		} else {
			Integer response = service.update(table, id, Record.valueOf(record), new Params(params));
			if (response == null) {
				return error(ErrorCode.CANNOT_UPDATE_RECORD, record.toString());
			}
			return success(response);
		}
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Deleting record from {} with id {}", table, id);
		if (!service.exists(table)) {
			return error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		if (id.indexOf(',') >= 0) {
			ArrayList<Object> result = new ArrayList<>();
			for (String s : id.split(",")) {
				result.add(service.delete(table, s, new Params(params)));
			}
			return success(result);
		} else {
			Integer response = service.delete(table, id, new Params(params));
			if (response == null) {
				return error(ErrorCode.CANNOT_DELETE_RECORD, id);
			}
			return success(response);
		}
	}

}
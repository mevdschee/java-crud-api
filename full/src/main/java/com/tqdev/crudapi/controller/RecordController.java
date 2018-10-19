package com.tqdev.crudapi.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.record.RecordService;
import com.tqdev.crudapi.record.container.Record;
import com.tqdev.crudapi.record.ErrorCode;
import com.tqdev.crudapi.record.Params;

@RestController
@RequestMapping("/records")
public class RecordController {

	public static final Logger logger = LoggerFactory.getLogger(RecordController.class);

	@Autowired
	Responder responder;

	@Autowired
	RecordService service;

	@RequestMapping(value = "/{table}", method = RequestMethod.GET)
	public ResponseEntity<?> list(@PathVariable("table") String table,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Listing table with name {} and parameters {}", table, params);
		if (!service.exists(table)) {
			return responder.error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		return responder.success(service.list(table, new Params(params)));
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> read(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Reading record from {} with id {} and parameters {}", table, id, params);
		if (!service.exists(table)) {
			return responder.error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		if (id.indexOf(',') >= 0) {
			String[] ids = id.split(",");
			ArrayList<Object> result = new ArrayList<>();
			for (int i = 0; i < ids.length; i++) {
				result.add(service.read(table, ids[i], new Params(params)));
			}
			return responder.success(result);
		} else {
			Object response = service.read(table, id, new Params(params));
			if (response == null) {
				return responder.error(ErrorCode.RECORD_NOT_FOUND, id);
			}
			return responder.success(response);
		}
	}

	@RequestMapping(value = "/{table}", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public ResponseEntity<?> create(@PathVariable("table") String table,
			@RequestBody LinkedMultiValueMap<String, String> record,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		ObjectMapper mapper = new ObjectMapper();
		Object pojo = mapper.convertValue(convertToSingleValueMap(record), Object.class);
		return create(table, pojo, params);
	}

	@RequestMapping(value = "/{table}", method = RequestMethod.POST, headers = "Content-Type=application/json")
	public ResponseEntity<?> create(@PathVariable("table") String table, @RequestBody Object record,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Creating record in {} with properties {}", table, record);
		if (!service.exists(table)) {
			return responder.error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		if (record instanceof ArrayList<?>) {
			ArrayList<?> records = (ArrayList<?>) record;
			ArrayList<Object> result = new ArrayList<>();
			for (int i = 0; i < records.size(); i++) {
				result.add(service.create(table, Record.valueOf(records.get(i)), new Params(params)));
			}
			return responder.success(result);
		} else {
			return responder.success(service.create(table, Record.valueOf(record), new Params(params)));
		}
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> convertToSingleValueMap(LinkedMultiValueMap<String, String> map) {
		LinkedHashMap<String, Object> result = new LinkedHashMap<>();
		for (String key : map.keySet()) {
			for (String v : map.get(key)) {
				Object value = v;
				if (key.endsWith("__is_null")) {
					key = key.substring(0, key.indexOf("__is_null"));
					value = null;
				}
				if (result.containsKey(key)) {
					Object current = result.get(key);
					if (current.getClass().isArray()) {
						((ArrayList<Object>) current).add(value);
					} else {
						ArrayList<Object> arr = new ArrayList<>();
						arr.add(current);
						arr.add(v);
						value = arr;
					}
				}
				result.put(key, value);
			}
		}
		return result;
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.PUT, headers = "Content-Type=application/x-www-form-urlencoded")
	public ResponseEntity<?> update(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestBody LinkedMultiValueMap<String, String> record,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		ObjectMapper mapper = new ObjectMapper();
		Object pojo = mapper.convertValue(convertToSingleValueMap(record), Object.class);
		return update(table, id, pojo, params);
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.PUT, headers = "Content-Type=application/json")
	public ResponseEntity<?> update(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestBody Object record, @RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Inrementing record in {} with id {} and properties {}", table, id, record);
		if (!service.exists(table)) {
			return responder.error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		String[] ids = id.split(",");
		if (record instanceof ArrayList<?>) {
			ArrayList<?> records = (ArrayList<?>) record;
			if (ids.length != records.size()) {
				return responder.error(ErrorCode.ARGUMENT_COUNT_MISMATCH, id);
			}
			ArrayList<Object> result = new ArrayList<>();
			for (int i = 0; i < ids.length; i++) {
				result.add(service.update(table, ids[i], Record.valueOf(records.get(i)), new Params(params)));
			}
			return responder.success(result);
		} else {
			if (ids.length != 1) {
				return responder.error(ErrorCode.ARGUMENT_COUNT_MISMATCH, id);
			}
			return responder.success(service.update(table, id, Record.valueOf(record), new Params(params)));
		}
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.PATCH, headers = "Content-Type=application/x-www-form-urlencoded")
	public ResponseEntity<?> increment(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestBody LinkedMultiValueMap<String, String> record,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		ObjectMapper mapper = new ObjectMapper();
		Object pojo = mapper.convertValue(convertToSingleValueMap(record), Object.class);
		return update(table, id, pojo, params);
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.PATCH, headers = "Content-Type=application/json")
	public ResponseEntity<?> increment(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestBody Object record, @RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Updating record in {} with id {} and properties {}", table, id, record);
		if (!service.exists(table)) {
			return responder.error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		String[] ids = id.split(",");
		if (record instanceof ArrayList<?>) {
			ArrayList<?> records = (ArrayList<?>) record;
			if (ids.length != records.size()) {
				return responder.error(ErrorCode.ARGUMENT_COUNT_MISMATCH, id);
			}
			ArrayList<Object> result = new ArrayList<>();
			for (int i = 0; i < ids.length; i++) {
				result.add(service.increment(table, ids[i], Record.valueOf(records.get(i)), new Params(params)));
			}
			return responder.success(result);
		} else {
			if (ids.length != 1) {
				return responder.error(ErrorCode.ARGUMENT_COUNT_MISMATCH, id);
			}
			return responder.success(service.increment(table, id, Record.valueOf(record), new Params(params)));
		}
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestParam LinkedMultiValueMap<String, String> params) {
		logger.info("Deleting record from {} with id {}", table, id);
		if (!service.exists(table)) {
			return responder.error(ErrorCode.TABLE_NOT_FOUND, table);
		}
		String[] ids = id.split(",");
		if (ids.length > 1) {
			ArrayList<Object> result = new ArrayList<>();
			for (int i = 0; i < ids.length; i++) {
				result.add(service.delete(table, ids[i], new Params(params)));
			}
			return responder.success(result);
		} else {
			return responder.success(service.delete(table, id, new Params(params)));
		}
	}

}
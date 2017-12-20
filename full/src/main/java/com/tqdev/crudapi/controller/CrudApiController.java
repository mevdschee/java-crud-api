package com.tqdev.crudapi.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.Record;
import com.tqdev.crudapi.service.ListResponse;

@RestController
@RequestMapping("/data")
public class CrudApiController {

	public static final Logger logger = LoggerFactory.getLogger(CrudApiController.class);

	@Autowired
	CrudApiService service;

	@RequestMapping(value = "/{table}", method = RequestMethod.GET)
	public ResponseEntity<?> list(@PathVariable("table") String table) {
		logger.info("Listing table with name {}", table);
		ListResponse response = service.list(table);
		if (response == null) {
			return new ResponseEntity<>("table", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> read(@PathVariable("table") String table, @PathVariable("id") String id) {
		logger.info("Reading record from {} with id {}", table, id);
		if (id.indexOf(',') >= 0) {
			ArrayList<Object> result = new ArrayList<>();
			for (String s : id.split(",")) {
				result.add(service.read(table, s));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			Object response = service.read(table, id);
			if (response == null) {
				return new ResponseEntity<>("object", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{table}", method = RequestMethod.POST)
	public ResponseEntity<?> create(@PathVariable("table") String table, @RequestBody Object record) {
		logger.info("Creating record in {} with properties {}", table, record);
		if (record instanceof ArrayList<?>) {
			ArrayList<Object> result = new ArrayList<>();
			for (Object o : (ArrayList<?>) record) {
				result.add(service.create(table, Record.valueOf(o)));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			String response = service.create(table, Record.valueOf(record));
			if (response == null) {
				return new ResponseEntity<>("input", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> update(@PathVariable("table") String table, @PathVariable("id") String id,
			@RequestBody Object record) {
		logger.info("Updating record in {} with id {} and properties {}", table, id, record);
		if (id.indexOf(',') >= 0 && record instanceof ArrayList<?>) {
			ArrayList<Object> result = new ArrayList<>();
			String[] ids = id.split(",");
			ArrayList<?> records = new ArrayList<>();
			if (ids.length != records.size()) {
				return new ResponseEntity<>("subject", HttpStatus.NOT_FOUND);
			}
			for (int i = 0; i < ids.length; i++) {
				result.add(service.update(table, ids[i], Record.valueOf(records.get(i))));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			Integer response = service.update(table, id, Record.valueOf(record));
			if (response == null) {
				return new ResponseEntity<>("subject", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("table") String table, @PathVariable("id") String id) {
		logger.info("Deleting record from {} with id {}", table, id);
		if (id.indexOf(',') >= 0) {
			ArrayList<Object> result = new ArrayList<>();
			for (String s : id.split(",")) {
				result.add(service.delete(table, s));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			Integer response = service.delete(table, id);
			if (response == null) {
				return new ResponseEntity<>("object", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

}
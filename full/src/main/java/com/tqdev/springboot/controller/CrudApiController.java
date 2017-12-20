package com.tqdev.springboot.controller;

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

import com.tqdev.springboot.service.CrudApiService;
import com.tqdev.springboot.service.ListResponse;

@RestController
@RequestMapping("/data")
public class CrudApiController {

	public static final Logger logger = LoggerFactory.getLogger(CrudApiController.class);

	@Autowired
	CrudApiService service;

	@RequestMapping(value = "/{entity}", method = RequestMethod.GET)
	public ResponseEntity<?> list(@PathVariable("entity") String entity) {
		logger.info("Listing entity with name {}", entity);
		ListResponse response = service.list(entity);
		if (response == null) {
			return new ResponseEntity<>("entity", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> read(@PathVariable("entity") String entity, @PathVariable("id") String id) {
		logger.info("Reading record from {} with id {}", entity, id);
		if (id.indexOf(',') >= 0) {
			ArrayList<Object> result = new ArrayList<>();
			for (String s : id.split(",")) {
				result.add(service.read(entity, s));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			Object response = service.read(entity, id);
			if (response == null) {
				return new ResponseEntity<>("object", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{entity}", method = RequestMethod.POST)
	public ResponseEntity<?> create(@PathVariable("entity") String entity, @RequestBody Object record) {
		logger.info("Creating record in {} with properties {}", entity, record);
		if (record instanceof ArrayList<?>) {
			ArrayList<Object> result = new ArrayList<>();
			for (Object o : (ArrayList<?>) record) {
				result.add(service.create(entity, o));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			String response = service.create(entity, record);
			if (response == null) {
				return new ResponseEntity<>("input", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> update(@PathVariable("entity") String entity, @PathVariable("id") String id,
			@RequestBody Object record) {
		logger.info("Updating record in {} with id {} and properties {}", entity, id, record);
		if (id.indexOf(',') >= 0 && record instanceof ArrayList<?>) {
			ArrayList<Object> result = new ArrayList<>();
			String[] ids = id.split(",");
			ArrayList<?> records = new ArrayList<>();
			if (ids.length != records.size()) {
				return new ResponseEntity<>("subject", HttpStatus.NOT_FOUND);
			}
			for (int i = 0; i < ids.length; i++) {
				result.add(service.update(entity, ids[i], records.get(i)));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			Integer response = service.update(entity, id, record);
			if (response == null) {
				return new ResponseEntity<>("subject", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("entity") String entity, @PathVariable("id") String id) {
		logger.info("Deleting record from {} with id {}", entity, id);
		if (id.indexOf(',') >= 0) {
			ArrayList<Object> result = new ArrayList<>();
			for (String s : id.split(",")) {
				result.add(service.delete(entity, s));
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			Integer response = service.delete(entity, id);
			if (response == null) {
				return new ResponseEntity<>("object", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

}
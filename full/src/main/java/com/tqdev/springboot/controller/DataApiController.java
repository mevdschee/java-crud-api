package com.tqdev.springboot.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.tqdev.springboot.service.DataApiService;
import com.tqdev.springboot.service.ListResponse;
import com.tqdev.springboot.util.DataApiError;

@RestController
@RequestMapping("/data")
public class DataApiController {

	public static final Logger logger = LoggerFactory.getLogger(DataApiController.class);

	@Autowired
	DataApiService service; // Service which will do all data retrieval/manipulation work

	@RequestMapping(value = "/{entity}", method = RequestMethod.GET)
	public ResponseEntity<?> list(@PathVariable("entity") String entity) {
		ListResponse result = service.list(entity);
		if (result == null) {
			logger.error("Unable to list {}", entity);
			return new ResponseEntity<>(new DataApiError("Unable to list " + entity), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> read(@PathVariable("entity") String entity, @PathVariable("id") String id) {
		logger.info("Fetching Object with id {}", id);
		Object record = service.read(entity, id);
		if (record == null) {
			logger.error("Object with id {} not found.", id);
			return new ResponseEntity<>(new DataApiError("Object with id " + id + " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(record, HttpStatus.OK);
	}

	@RequestMapping(value = "/{entity}", method = RequestMethod.POST)
	public ResponseEntity<?> create(@PathVariable("entity") String entity, @RequestBody Object record,
			UriComponentsBuilder ucBuilder) {
		logger.info("Creating {} : {}", entity, record);

		String id = service.create(entity, record);
		if (id == null) {
			logger.error("Unable to create. A Object with properties {} already exist", record);
			return new ResponseEntity<>(
					new DataApiError("Unable to create. A Object with properties " + record + " already exist."),
					HttpStatus.CONFLICT);
		}

		HttpHeaders headers = new HttpHeaders();
		HashMap<String, Object> vars = new HashMap<>();
		vars.put("entity", entity);
		vars.put("id", id);
		headers.setLocation(ucBuilder.path("/data/{entity}/{id}").buildAndExpand(vars).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> update(@PathVariable("entity") String entity, @PathVariable("id") String id,
			@RequestBody Object record) {
		logger.info("Updating Object with id {}", id);

		int modified = service.update(entity, id, record);

		if (modified == 0) {
			logger.error("Unable to update. Object with id {} not found.", id);
			return new ResponseEntity<>(new DataApiError("Unable to upate. Object with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Object>(record, HttpStatus.OK);
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("entity") String entity, @PathVariable("id") String id) {
		logger.info("Fetching & Deleting Object with id {}", id);

		int deleted = service.delete(entity, id);
		if (deleted == 0) {
			logger.error("Unable to delete. Object with id {} not found.", id);
			return new ResponseEntity<>(new DataApiError("Unable to delete. Object with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
	}

}
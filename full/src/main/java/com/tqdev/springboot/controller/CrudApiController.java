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
import org.springframework.web.util.UriComponentsBuilder;

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
			logger.warn("Entity with name {} not found", entity);
			return new ResponseEntity<>("entity", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> read(@PathVariable("entity") String entity, @PathVariable("id") String ids) {
		if (ids.indexOf(',') >= 0) {
			logger.info("Reading records from {} with ids {}", entity, ids);
			ArrayList<Object> result = new ArrayList<>();
			for (String id : ids.split(",")) {
				Object response = service.read(entity, id);
				if (response == null) {
					logger.warn("Reading record from {} with id {} failed", entity, id);
				}
				result.add(response);
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			String id = ids;
			logger.info("Reading record from {} with id {}", entity, id);
			Object response = service.read(entity, id);
			if (response == null) {
				logger.warn("Reading record from {} with id {} failed", entity, id);
				return new ResponseEntity<>("object", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{entity}", method = RequestMethod.POST)
	public ResponseEntity<?> create(@PathVariable("entity") String entity, @RequestBody Object record,
			UriComponentsBuilder ucBuilder) {
		logger.info("Creating {}: {}", entity, record);
		String response = service.create(entity, record);
		if (response == null) {
			logger.error("Unable to create record with properties {}", record);
			return new ResponseEntity<>("input", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> update(@PathVariable("entity") String entity, @PathVariable("id") String id,
			@RequestBody Object record) {
		logger.info("Updating record with id {}", id);
		Integer response = service.update(entity, id, record);
		if (response == null) {
			logger.error("Unable to update record with id {}", id);
			return new ResponseEntity<>("subject", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("entity") String entity, @PathVariable("id") String id) {
		logger.info("Fetching & Deleting Object with id {}", id);

		Integer response = service.delete(entity, id);
		if (response == null) {
			logger.error("Unable to delete record with id {}", id);
			return new ResponseEntity<>("object", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
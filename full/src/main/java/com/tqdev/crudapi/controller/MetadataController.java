package com.tqdev.crudapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tqdev.crudapi.service.CrudApiService;

@RestController
@RequestMapping("/meta")
public class MetadataController {

	public static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

	@Autowired
	CrudApiService service;

	@RequestMapping(value = "/columns", method = RequestMethod.GET)
	public ResponseEntity<?> columns() {
		logger.info("Requesting columns meta data");
		return new ResponseEntity<>(service.getDatabaseDefinition(), HttpStatus.OK);
	}

	@RequestMapping(value = "/records", method = RequestMethod.GET)
	public ResponseEntity<?> records() {
		logger.info("Requesting records meta data");
		return new ResponseEntity<>(service.getDatabaseRecords(), HttpStatus.OK);
	}

}
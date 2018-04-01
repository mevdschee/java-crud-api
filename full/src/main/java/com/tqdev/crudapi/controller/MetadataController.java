package com.tqdev.crudapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tqdev.crudapi.api.CrudApiService;
import com.tqdev.crudapi.meta.CrudMetaService;

@RestController
@RequestMapping("/meta")
public class MetadataController extends BaseController {

	public static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

	@Autowired
	CrudApiService crudService;

	@Autowired
	CrudMetaService metaService;

	@RequestMapping(value = "/columns", method = RequestMethod.GET)
	public ResponseEntity<?> columns() {
		logger.info("Requesting columns meta data");
		return success(metaService.getDatabaseDefinition());
	}

	@RequestMapping(value = "/records", method = RequestMethod.GET)
	public ResponseEntity<?> records() {
		logger.info("Requesting records meta data");
		return success(crudService.getDatabaseRecords());
	}

	@RequestMapping(value = "/openapi", method = RequestMethod.GET)
	public ResponseEntity<?> openapi() {
		logger.info("Requesting openapi meta data");
		return success(metaService.getOpenApiDefinition());
	}
}
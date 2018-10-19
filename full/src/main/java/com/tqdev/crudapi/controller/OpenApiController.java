package com.tqdev.crudapi.controller;

import com.tqdev.crudapi.openapi.OpenApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openapi")
public class OpenApiController {

	public static final Logger logger = LoggerFactory.getLogger(OpenApiController.class);

	@Autowired
	Responder responder;

	@Autowired
	OpenApiService service;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<?> openapi() {
		logger.info("Requesting openapi meta data");
		return responder.success(service.getOpenApiDefinition());
	}
}
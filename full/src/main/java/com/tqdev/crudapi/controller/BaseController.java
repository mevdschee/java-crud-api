package com.tqdev.crudapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tqdev.crudapi.crud.ErrorCode;
import com.tqdev.crudapi.crud.record.ErrorDocument;

public class BaseController {

	protected ResponseEntity<?> error(ErrorCode error, String argument) {
		return new ResponseEntity<>(new ErrorDocument(error, argument), error.getStatus());
	}

	protected ResponseEntity<?> success(Object result) {
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}

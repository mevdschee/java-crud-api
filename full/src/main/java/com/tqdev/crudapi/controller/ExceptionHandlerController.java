package com.tqdev.crudapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerMapping;

import com.tqdev.crudapi.record.ErrorCode;

@RestController
@RestControllerAdvice
public class ExceptionHandlerController {

	@Autowired
	Responder responder;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> exceptionHandler(Exception ex, HttpServletRequest request) {
		ErrorCode error = ErrorCode.ERROR_NOT_FOUND;
		String argument = ex.getClass().getSimpleName();
		switch (argument) {
		case "HttpMessageNotReadableException":
			error = ErrorCode.HTTP_MESSAGE_NOT_READABLE;
			argument = null;
			break;
		case "DuplicateKeyException":
			error = ErrorCode.DUPLICATE_KEY_EXCEPTION;
			argument = null;
			break;
		case "DataIntegrityViolationException":
			error = ErrorCode.DATA_INTEGRITY_VIOLATION;
			argument = null;
			break;
		default:
			ex.printStackTrace();
		}
		return responder.error(error, argument);
	}

	@RequestMapping(value = "/**")
	public ResponseEntity<?> fallbackHandler(HttpServletRequest request) throws Exception {
		return responder.error(ErrorCode.ROUTE_NOT_FOUND,
				(String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
	}
}
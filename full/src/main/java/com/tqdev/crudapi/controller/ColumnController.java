package com.tqdev.crudapi.controller;

import com.tqdev.crudapi.column.ColumnService;
import com.tqdev.crudapi.column.reflection.ReflectedTable;
import com.tqdev.crudapi.record.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/columns")
public class ColumnController extends BaseController {

	public static final Logger logger = LoggerFactory.getLogger(ColumnController.class);

	@Autowired
	ColumnService service;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<?> getDatabase() {
		logger.info("Requesting columns meta data");
		return success(service.getDatabaseDefinition());
	}

	@RequestMapping(value = "/{table}", method = RequestMethod.GET)
	public ResponseEntity<?> getTable(@PathVariable("table") String tableName) {
		logger.info("Requesting columns meta data");
		if (!service.hasTable(tableName)) {
			return error(ErrorCode.TABLE_NOT_FOUND, tableName);
		}
		return success(service.getDatabaseDefinition().get(tableName));
	}

	@RequestMapping(value = "/{table}/{column}", method = RequestMethod.GET)
	public ResponseEntity<?> getColumn(@PathVariable("table") String tableName, @PathVariable("column") String columnName) {
		logger.info("Requesting columns meta data");
		if (!service.hasTable(tableName)) {
			return error(ErrorCode.TABLE_NOT_FOUND, tableName);
		}
		ReflectedTable table = service.getTable(tableName);
		if (!table.exists(columnName)) {
			return error(ErrorCode.COLUMN_NOT_FOUND, columnName);
		}
		return success(table.get(columnName));
	}
}
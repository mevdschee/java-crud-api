package com.tqdev.crudapi.service;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	TABLE_NOT_FOUND(1001, "Table '%s' not found", HttpStatus.NOT_FOUND),

	ARGUMENT_COUNT_MISMATCH(1002, "Argument count mismatch in '%s'", HttpStatus.NOT_ACCEPTABLE),

	RECORD_NOT_FOUND(1003, "Record '%s' not found", HttpStatus.NOT_FOUND),

	CANNOT_LIST_TABLE(1004, "Cannot list table '%s'", HttpStatus.NOT_ACCEPTABLE),

	CANNOT_CREATE_RECORD(1005, "Cannot create record '%s'", HttpStatus.NOT_ACCEPTABLE),

	CANNOT_UPDATE_RECORD(1006, "Cannot update record '%s'", HttpStatus.NOT_ACCEPTABLE),

	CANNOT_DELETE_RECORD(1007, "Cannot delete record '%s'", HttpStatus.NOT_ACCEPTABLE);

	private final int code;

	private final String message;

	private final HttpStatus status;

	ErrorCode(int code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}

	/**
	 * Return the integer value of this error code.
	 */
	public int value() {
		return this.code;
	}

	/**
	 * Return the message of this error code.
	 */
	public String getMessage(String argument) {
		return String.format(this.message, argument);
	}

	/**
	 * Return the status of this error code.
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * Return a string representation of this error code.
	 */
	@Override
	public String toString() {
		return Integer.toString(this.code);
	}

}

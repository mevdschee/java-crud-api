package com.tqdev.crudapi.record;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	ERROR_NOT_FOUND(9999, "%s", HttpStatus.INTERNAL_SERVER_ERROR),

	ROUTE_NOT_FOUND(1000, "Route '%s' not found", HttpStatus.NOT_FOUND),

	TABLE_NOT_FOUND(1001, "Table '%s' not found", HttpStatus.NOT_FOUND),

	ARGUMENT_COUNT_MISMATCH(1002, "Argument count mismatch in '%s'", HttpStatus.NOT_ACCEPTABLE),

	RECORD_NOT_FOUND(1003, "Record '%s' not found", HttpStatus.NOT_FOUND),

	ORIGIN_FORBIDDEN(1004, "Origin '%s' is forbidden", HttpStatus.FORBIDDEN),

	COLUMN_NOT_FOUND(1005, "Column '%s' not found", HttpStatus.NOT_FOUND),

	HTTP_MESSAGE_NOT_READABLE(1008, "Cannot read HTTP message", HttpStatus.NOT_ACCEPTABLE),

	DUPLICATE_KEY_EXCEPTION(1009, "Duplicate key exception", HttpStatus.NOT_ACCEPTABLE),

	DATA_INTEGRITY_VIOLATION(1010, "Data integrity violation", HttpStatus.NOT_ACCEPTABLE);

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

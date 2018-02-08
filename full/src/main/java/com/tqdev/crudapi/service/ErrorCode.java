package com.tqdev.crudapi.service;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	TABLE_NOT_FOUND(1, "Table '%s' not found", HttpStatus.NOT_FOUND);

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

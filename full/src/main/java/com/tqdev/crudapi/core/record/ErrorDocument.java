package com.tqdev.crudapi.core.record;

import com.tqdev.crudapi.core.ErrorCode;

public class ErrorDocument {

	protected final int code;

	protected final String message;

	public ErrorDocument(ErrorCode error, String argument) {
		this.code = error.value();
		this.message = error.getMessage(argument);
	}

	/**
	 * Return the code of this error.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Return the message of this error.
	 */
	public String getMessage() {
		return message;
	}

}

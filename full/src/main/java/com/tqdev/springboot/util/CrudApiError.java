package com.tqdev.springboot.util;

public class CrudApiError {

	private String message;

	public CrudApiError(String message) {
		this.message = message;
	}

	public String getErrorMessage() {
		return message;
	}

}

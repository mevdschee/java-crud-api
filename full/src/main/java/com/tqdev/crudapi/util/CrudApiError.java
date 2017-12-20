package com.tqdev.crudapi.util;

public class CrudApiError {

	private String message;

	public CrudApiError(String message) {
		this.message = message;
	}

	public String getErrorMessage() {
		return message;
	}

}

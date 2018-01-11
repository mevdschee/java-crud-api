package com.tqdev.crudapi.service;

import java.util.List;

import org.springframework.util.LinkedMultiValueMap;

public class Params extends LinkedMultiValueMap<String, List<String>> {

	public Params(LinkedMultiValueMap<String, List<String>> params) {
		super(params);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

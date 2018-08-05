package com.tqdev.crudapi.record;

import org.springframework.util.LinkedMultiValueMap;

public class Params extends LinkedMultiValueMap<String, String> {

	public Params() {
		super();
	}

	public Params(LinkedMultiValueMap<String, String> params) {
		super(params);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

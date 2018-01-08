package com.tqdev.crudapi.service;

import java.util.LinkedHashMap;

public class TableDefinition extends LinkedHashMap<String, ColumnDefinition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getPk() {
		for (String key : keySet()) {
			if (get(key).getPk() == true) {
				return key;
			}
		}
		return null;
	}
}

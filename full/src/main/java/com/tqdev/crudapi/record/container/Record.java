package com.tqdev.crudapi.record.container;

import java.util.LinkedHashMap;
import java.util.Map;

public class Record extends LinkedHashMap<String, Object> {

	public static Record valueOf(Object object) {
		if (object instanceof Map<?, ?>) {
			return Record.valueOf((Map<?, ?>) object);
		}
		return null;
	}

	public static Record valueOf(Map<?, ?> map) {
		if (map != null) {
			Record result = new Record();
			for (Object key : map.keySet()) {
				result.put(key.toString(), map.get(key));
			}
			return result;
		}
		return null;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

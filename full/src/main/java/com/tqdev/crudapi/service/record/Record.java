package com.tqdev.crudapi.service.record;

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

	public static Record valueOf(org.jooq.Record record) {
		if (record != null) {
			return Record.valueOf(record.intoMap());
		}
		return null;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

package com.tqdev.crudapi.service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class BaseCrudApiService {

	protected Set<String> columns(String table, Set<String> keySet, Params params) {
		if (!params.containsKey("columns")) {
			return keySet;
		}
		HashMap<String, Boolean> columns = new HashMap<>();
		for (String key : params.get("columns").get(0).split(",")) {
			columns.put(key, true);
		}
		LinkedHashSet<String> result = new LinkedHashSet<>();
		for (String key : keySet) {
			if (columns.containsKey("*.*") || columns.containsKey(table + ".*")
					|| columns.containsKey(table + "." + key) || columns.containsKey("*") || columns.containsKey(key)) {
				result.add(key);
			}
		}
		return result;
	}
}

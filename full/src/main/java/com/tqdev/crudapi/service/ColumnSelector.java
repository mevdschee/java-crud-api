package com.tqdev.crudapi.service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.tqdev.crudapi.service.definition.DatabaseDefinition;

public interface ColumnSelector {

	default Set<String> columns(String table, Params params, DatabaseDefinition definition) {
		if (!params.containsKey("columns")) {
			return definition.get(table).keySet();
		}
		HashMap<String, Boolean> columns = new HashMap<>();
		for (String key : params.get("columns").get(0).split(",")) {
			columns.put(key, true);
		}
		LinkedHashSet<String> result = new LinkedHashSet<>();
		for (String key : definition.get(table).keySet()) {
			if (columns.containsKey("*.*") || columns.containsKey(table + ".*")
					|| columns.containsKey(table + "." + key) || columns.containsKey("*") || columns.containsKey(key)) {
				result.add(key);
			}
		}
		return result;
	}
}
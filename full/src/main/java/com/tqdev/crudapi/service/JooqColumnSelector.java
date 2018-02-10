package com.tqdev.crudapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.record.Record;
import com.tqdev.crudapi.spatial.SpatialDSL;

public interface JooqColumnSelector {

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

	default public LinkedHashMap<Field<?>, Object> columnValues(String table, Record record, Params params,
			DatabaseDefinition definition) {
		LinkedHashMap<Field<?>, Object> columns = new LinkedHashMap<>();
		Set<String> cols = columns(table, params, definition);
		for (String key : cols) {
			if (record.containsKey(key)) {
				if (definition.get(table).get(key).getType().equals("geometry")) {
					columns.put(DSL.field(key), SpatialDSL.geomFromText(DSL.val(record.get(key))));
				} else {
					columns.put(DSL.field(key), record.get(key));
				}
			}
		}
		return columns;
	}

	default public ArrayList<Field<?>> columnNames(String table, Params params, DatabaseDefinition definition) {
		ArrayList<Field<?>> columns = new ArrayList<>();
		for (String key : columns(table, params, definition)) {
			if (definition.get(table).get(key).getType().equals("geometry")) {
				columns.add(SpatialDSL.asText(DSL.field(key)).as(key));
			} else {
				columns.add(DSL.field(key));
			}
		}
		return columns;
	}

}
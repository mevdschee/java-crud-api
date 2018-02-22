package com.tqdev.crudapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.core.record.Record;
import com.tqdev.crudapi.meta.reflection.ReflectedTable;
import com.tqdev.crudapi.spatial.SpatialDSL;

public interface JooqColumnSelector {

	default Set<String> exclude(ReflectedTable table, Params params, Set<String> fieldNames) {
		if (!params.containsKey("exclude")) {
			return fieldNames;
		}
		HashMap<String, Boolean> columns = new HashMap<>();
		for (String key : params.get("exclude").get(0).split(",")) {
			columns.put(key, true);
		}
		LinkedHashSet<String> result = new LinkedHashSet<>();
		for (String key : fieldNames) {
			if (!(columns.containsKey("*.*") || columns.containsKey(table.getName() + ".*")
					|| columns.containsKey(table.getName() + "." + key) || columns.containsKey("*")
					|| columns.containsKey(key))) {
				result.add(key);
			}
		}
		return result;
	}

	default Set<String> columns(ReflectedTable table, Params params) {
		if (!params.containsKey("columns")) {
			return exclude(table, params, table.fieldNames());
		}
		HashMap<String, Boolean> columns = new HashMap<>();
		for (String key : params.get("columns").get(0).split(",")) {
			columns.put(key, true);
		}
		LinkedHashSet<String> result = new LinkedHashSet<>();
		for (String key : table.fieldNames()) {
			if (columns.containsKey("*.*") || columns.containsKey(table.getName() + ".*")
					|| columns.containsKey(table.getName() + "." + key) || columns.containsKey("*")
					|| columns.containsKey(key)) {
				result.add(key);
			}
		}
		return exclude(table, params, result);
	}

	default public LinkedHashMap<Field<?>, Object> columnValues(ReflectedTable table, Record record, Params params) {
		LinkedHashMap<Field<?>, Object> columns = new LinkedHashMap<>();
		Set<String> cols = columns(table, params);
		for (String key : cols) {
			if (record.containsKey(key)) {
				Field<Object> field = table.get(key);
				if (field.getDataType().getTypeName().equals("geometry")) {
					columns.put(field, SpatialDSL.geomFromText(DSL.val(record.get(key))));
				} else {
					columns.put(field, record.get(key));
				}
			}
		}
		return columns;
	}

	default public LinkedHashMap<Field<?>, Object> columnIncrements(ReflectedTable table, Record record,
			Params params) {
		LinkedHashMap<Field<?>, Object> columns = new LinkedHashMap<>();
		Set<String> cols = columns(table, params);
		for (String key : cols) {
			if (record.containsKey(key)) {
				Field<Object> field = table.get(key);
				Object value = record.get(key);
				if (value instanceof Number) {
					columns.put(field, field.add((Number) value));
				}
			}
		}
		return columns;
	}

	default public ArrayList<Field<?>> columnNames(ReflectedTable table, Params params) {
		ArrayList<Field<?>> columns = new ArrayList<>();
		for (String key : columns(table, params)) {
			Field<?> field = table.get(key);
			if (field.getDataType().getTypeName().equals("geometry")) {
				columns.add(SpatialDSL.asText(field).as(key));
			} else {
				columns.add(field);
			}
		}
		return columns;
	}

}
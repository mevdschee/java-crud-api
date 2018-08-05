package com.tqdev.crudapi.record;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.column.reflection.ReflectedTable;
import com.tqdev.crudapi.record.container.Record;
import com.tqdev.crudapi.record.spatial.SpatialDSL;

public class ColumnSelector {

	private boolean isMandatoryField(String tableName, String fieldName, Params params) {
		return params.containsKey("mandatory") && params.get("mandatory").contains(tableName + "." + fieldName);
	}

	private Set<String> select(String tableName, boolean primaryTable, Params params, String paramName,
			Set<String> fieldNames, boolean include) {
		if (!params.containsKey(paramName)) {
			return fieldNames;
		}
		HashMap<String, Boolean> columns = new HashMap<>();
		for (String key : params.get(paramName).get(0).split(",")) {
			columns.put(key, true);
		}
		LinkedHashSet<String> result = new LinkedHashSet<>();
		for (String key : fieldNames) {
			boolean match = columns.containsKey("*.*");
			if (!match) {
				match = columns.containsKey(tableName + ".*") || columns.containsKey(tableName + "." + key);
			}
			if (primaryTable && !match) {
				match = columns.containsKey("*") || columns.containsKey(key);
			}
			if (match) {
				if (include || isMandatoryField(tableName, key, params)) {
					result.add(key);
				}
			} else {
				if (!include || isMandatoryField(tableName, key, params)) {
					result.add(key);
				}
			}
		}
		return result;
	}

	private Set<String> columns(ReflectedTable table, boolean primaryTable, Params params) {
		String tableName = table.getName();
		Set<String> results = table.fieldNames();
		results = select(tableName, primaryTable, params, "columns", results, true);
		results = select(tableName, primaryTable, params, "exclude", results, false);
		return results;
	}

	public LinkedHashMap<Field<?>, Object> getValues(ReflectedTable table, boolean primaryTable, Record record,
													 Params params) {
		LinkedHashMap<Field<?>, Object> columns = new LinkedHashMap<>();
		Set<String> cols = columns(table, primaryTable, params);
		for (String key : cols) {
			if (record.containsKey(key)) {
				Field<Object> field = table.get(key);
				if (field.getDataType().getTypeName().equals("geometry")) {
					columns.put(field, SpatialDSL.geomFromText(DSL.val(record.get(key))));
				} else if (field.getDataType().isBinary() && record.get(key) != null) {
					columns.put(field, Base64.getDecoder().decode((String) record.get(key)));
				} else {
					columns.put(field, record.get(key));
				}
			}
		}
		return columns;
	}

	public LinkedHashMap<Field<?>, Object> getIncrements(ReflectedTable table, boolean primaryTable,
														 Record record, Params params) {
		LinkedHashMap<Field<?>, Object> columns = new LinkedHashMap<>();
		Set<String> cols = columns(table, primaryTable, params);
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

	public ArrayList<Field<?>> getNames(ReflectedTable table, boolean primaryTable, Params params) {
		ArrayList<Field<?>> columns = new ArrayList<>();
		for (String key : columns(table, primaryTable, params)) {
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
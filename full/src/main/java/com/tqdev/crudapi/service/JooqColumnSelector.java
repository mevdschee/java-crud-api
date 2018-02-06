package com.tqdev.crudapi.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.record.Record;
import com.tqdev.crudapi.spatial.SpatialDSL;

public interface JooqColumnSelector extends ColumnSelector {

	default public LinkedHashMap<Field<?>, Object> columnValues(String table, Record record, Params params,
			DatabaseDefinition definition) {
		LinkedHashMap<Field<?>, Object> columns = new LinkedHashMap<>();
		for (String key : columns(table, params, definition)) {
			if (record.containsKey(key)) {
				if (definition.get(table).get(key).getType() == "geometry") {
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
			if (definition.get(table).get(key).getType() == "geometry") {
				columns.add(SpatialDSL.asText(DSL.field(key)).as(key));
			} else {
				columns.add(DSL.field(key));
			}
		}
		return columns;
	}

}
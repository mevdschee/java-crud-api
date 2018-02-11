package com.tqdev.crudapi.service;

import java.util.ArrayList;

import org.jooq.SortField;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.service.definition.DatabaseDefinition;

public interface JooqOrdering {

	default public ArrayList<SortField<?>> ordering(String table, Params params, DatabaseDefinition definition) {
		ArrayList<SortField<?>> fields = new ArrayList<>();
		if (params.containsKey("order")) {
			for (String key : params.get("order")) {
				String[] parts = key.split(",", 2);
				boolean ascending = true;
				if (parts.length > 1) {
					ascending = !parts[1].toLowerCase().startsWith("desc");
				}
				if (ascending) {
					fields.add(DSL.field(DSL.name(parts[0])).asc());
				} else {
					fields.add(DSL.field(DSL.name(parts[0])).desc());
				}
			}
		}
		return fields;
	}

}
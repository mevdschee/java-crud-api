package com.tqdev.crudapi.core;

import java.util.ArrayList;

import org.jooq.SortField;

import com.tqdev.crudapi.meta.reflection.ReflectedTable;

public interface JooqOrdering {

	default public ArrayList<SortField<?>> ordering(ReflectedTable table, Params params) {
		ArrayList<SortField<?>> fields = new ArrayList<>();
		if (params.containsKey("order")) {
			for (String key : params.get("order")) {
				String[] parts = key.split(",", 2);
				boolean ascending = true;
				if (parts.length > 1) {
					ascending = !parts[1].toLowerCase().startsWith("desc");
				}
				if (ascending) {
					fields.add(table.get(parts[0]).asc());
				} else {
					fields.add(table.get(parts[0]).desc());
				}
			}
		}
		return fields;
	}

}
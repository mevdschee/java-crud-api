package com.tqdev.crudapi.service;

import java.util.ArrayList;

import org.jooq.SortField;
import org.jooq.impl.DSL;

public interface JooqOrdering {

	default public ArrayList<SortField<?>> ordering(Params params) {
		ArrayList<SortField<?>> fields = new ArrayList<>();
		if (params.containsKey("order")) {
			for (String key : params.get("order")) {
				String[] parts = key.split(",", 2);
				boolean ascending = true;
				if (parts.length > 1) {
					ascending = !parts[1].toLowerCase().startsWith("desc");
				}
				if (ascending) {
					fields.add(DSL.field(parts[0]).asc());
				} else {
					fields.add(DSL.field(parts[0]).desc());
				}
			}
		}
		return fields;
	}

}
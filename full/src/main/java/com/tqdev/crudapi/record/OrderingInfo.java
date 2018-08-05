package com.tqdev.crudapi.record;

import java.util.ArrayList;

import org.jooq.SortField;

import com.tqdev.crudapi.column.reflection.ReflectedTable;

public class OrderingInfo {

	public ArrayList<SortField<?>> getColumnOrdering(ReflectedTable table, Params params) {
		ArrayList<SortField<?>> fields = new ArrayList<>();
		if (params.containsKey("order")) {
			for (String key : params.get("order")) {
				String[] parts = key.split(",", 3);
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
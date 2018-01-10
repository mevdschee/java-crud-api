package com.tqdev.crudapi.service;

import java.util.LinkedHashMap;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UniqueKey;

public class TableDefinition extends LinkedHashMap<String, ColumnDefinition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getPk() {
		for (String key : keySet()) {
			if (get(key).getPk() == true) {
				return key;
			}
		}
		return null;
	}

	private static Field<?> findPrimaryKey(Table<?> table) {
		UniqueKey<?> pk = table.getPrimaryKey();
		if (pk == null) {
			// pk not found
			return null;
		}
		Field<?>[] pks = pk.getFieldsArray();
		if (pks.length > 1) {
			// multiple primary key error
			return null;
		}
		if (pks.length == 0) {
			// pk not found
			return null;
		}
		return pks[0];
	}

	public static TableDefinition fromValue(Table<?> table) {
		TableDefinition definition = new TableDefinition();
		for (Field<?> field : table.fields()) {
			definition.put(field.getName(), ColumnDefinition.fromValue(field));
		}
		Field<?> pk = findPrimaryKey(table);
		if (pk != null) {
			definition.get(pk.getName()).setPk(true);
		}
		// add fk values
		return definition;
	}
}

package com.tqdev.crudapi.service;

import java.util.LinkedHashMap;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
		if (pk != null) {
			Field<?>[] pks = pk.getFieldsArray();
			if (pks.length == 1) {
				return pks[0];
			}
		}
		return null;
	}

	private static Field<?> findForeignKeyField(ForeignKey<?, ?> fk) {
		Field<?>[] fks = fk.getFieldsArray();
		if (fks.length == 1) {
			return fks[0];
		}
		return null;
	}

	private static String findForeignKeyReference(ForeignKey<?, ?> fk) {
		UniqueKey<?> pk = fk.getKey();
		if (pk != null) {
			Field<?>[] pks = pk.getFieldsArray();
			if (pks.length == 1) {
				return pk.getTable().getName() + "." + pks[0].getName();
			}
		}
		return null;
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
		for (ForeignKey<?, ?> fk : table.getReferences()) {
			Field<?> field = findForeignKeyField(fk);
			String reference = findForeignKeyReference(fk);
			if (field != null && reference != null) {
				definition.get(field.getName()).setFk(reference);
			}
		}
		return definition;
	}
}

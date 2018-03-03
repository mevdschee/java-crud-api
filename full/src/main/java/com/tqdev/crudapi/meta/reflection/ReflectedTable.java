package com.tqdev.crudapi.meta.reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.CustomTable;

@SuppressWarnings("rawtypes")
public class ReflectedTable extends CustomTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Table<?> table;
	private HashMap<String, TableField<?, ?>> fields = new LinkedHashMap<>();
	private HashMap<String, String> fks = new LinkedHashMap<>();
	private TableField<?, ?> pk = null;

	@SuppressWarnings("unchecked")
	public ReflectedTable(Table<?> table) {
		super(table.getName());
		this.table = table;
		for (Field<?> field : table.fields()) {
			String name = field.getName();
			fields.put(name, createField(name, field.getDataType()));
		}
		UniqueKey<?> primaryKey = table.getPrimaryKey();
		if (primaryKey != null) {
			if (primaryKey.getFields().size() == 1) {
				pk = primaryKey.getFields().get(0);
			}
		}
		for (ForeignKey<?, ?> fk : table.getReferences()) {
			fks.put(findForeignKeyFieldName(fk), findForeignKeyReference(fk));
		}
	}

	@Override
	public Class<? extends Record> getRecordType() {
		return Record.class;
	}

	private TableField<?, ?> findPrimaryKey(Table<?> table) {
		UniqueKey<?> pk = table.getPrimaryKey();
		if (pk != null) {
			TableField<?, ?>[] pks = pk.getFieldsArray();
			if (pks.length == 1) {
				return pks[0];
			}
		}
		return null;
	}

	private String findForeignKeyFieldName(ForeignKey<?, ?> fk) {
		TableField<?, ?>[] pks = fk.getFieldsArray();
		if (pks.length == 1) {
			return pks[0].getName();
		}
		return null;
	}

	private String findForeignKeyReference(ForeignKey<?, ?> fk) {
		UniqueKey<?> pk = fk.getKey();
		if (pk != null) {
			Field<?>[] pks = pk.getFieldsArray();
			if (pks.length == 1) {
				return pk.getTable().getName();
			}
		}
		return null;
	}

	@Override
	public Identity getIdentity() {
		TableField<?, ?> pk = findPrimaryKey(table);
		if (pk == null) {
			return null;
		}
		return new DynamicIdentity(table, pk);
	}

	@SuppressWarnings("unchecked")
	public Field<Object> get(String field) {
		return (Field<Object>) fields.get(field);
	}

	@SuppressWarnings("unchecked")
	public Field<Object> getPk() {
		return (Field<Object>) pk;
	}

	public List<Field<Object>> getFks() {
		return getFksTo(null);
	}

	@SuppressWarnings("unchecked")
	public List<Field<Object>> getFksTo(String table) {
		List<Field<Object>> result = new ArrayList<>();
		for (String key : fks.keySet()) {
			if (table == null || fks.get(key).equals(table)) {
				result.add((Field<Object>) fields.get(key));
			}
		}
		return result;
	}

	public String getFk(String field) {
		return fks.get(field);
	}

	public Set<String> fieldNames() {
		return fields.keySet();
	}

	public boolean exists(String key) {
		return fields.containsKey(key);
	}

}
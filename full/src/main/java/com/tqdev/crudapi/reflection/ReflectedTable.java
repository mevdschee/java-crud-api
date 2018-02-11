package com.tqdev.crudapi.reflection;

import java.util.HashMap;
import java.util.Set;

import org.jooq.Field;
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
	private HashMap<String, TableField<?, ?>> fields = new HashMap<>();
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
	}

	@Override
	public Class<? extends Record> getRecordType() {
		return Record.class;
	}

	class DynamicIdentity<R extends Record, T> implements Identity {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Table<?> table;
		private TableField<?, ?> pk;

		public DynamicIdentity(Table<?> table, TableField<?, ?> pk) {
			this.table = table;
			this.pk = pk;
		}

		@Override
		public Table<?> getTable() {
			return table;
		}

		@SuppressWarnings("unchecked")
		@Override
		public TableField<R, T> getField() {
			return (TableField<R, T>) pk;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Identity getIdentity() {
		TableField<?, ?> pk = table.getPrimaryKey().getFields().get(0);
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

	public Set<String> fieldNames() {
		return fields.keySet();
	}

}
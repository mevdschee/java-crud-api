package com.tqdev.crudapi.service.definition;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.CustomTable;

@SuppressWarnings("rawtypes")
class ReflectedTable extends CustomTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Table<?> table;

	@SuppressWarnings("unchecked")
	public ReflectedTable(Table<?> table) {
		super(table.getName());
		this.table = table;
		for (Field<?> field : table.fields()) {
			createField(field.getName(), field.getDataType());
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
}
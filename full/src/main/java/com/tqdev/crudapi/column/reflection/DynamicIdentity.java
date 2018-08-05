package com.tqdev.crudapi.column.reflection;

import org.jooq.Identity;
import org.jooq.Table;
import org.jooq.TableField;

@SuppressWarnings("rawtypes")
class DynamicIdentity implements Identity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 59304360964854237L;
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

	@Override
	public TableField<?, ?> getField() {
		return pk;
	}
}

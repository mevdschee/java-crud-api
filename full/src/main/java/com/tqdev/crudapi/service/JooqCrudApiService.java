package com.tqdev.crudapi.service;

import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class JooqCrudApiService implements CrudApiService {

	private DatabaseDefinition definition = new DatabaseDefinition();

	private DSLContext dsl;

	public JooqCrudApiService(DSLContext dsl) {
		this.dsl = dsl;
		updateDefinition();
	}

	private Object convertType(ColumnDefinition column, Object value) {
		if (value == null) {
			return null;
		}
		switch (column.getType()) {
		case "string":
			if (!(value instanceof String)) {
				return String.valueOf(value);
			}
			break;
		case "integer":
			if (value instanceof String) {
				return Integer.parseInt((String) value);
			}
			break;
		}
		return value;
	}

	private void sanitizeRecord(String table, Record record) {
		for (String key : record.keySet()) {
			if (!definition.get(table).containsKey(key)) {
				record.remove(key);
			} else {
				ColumnDefinition column = definition.get(table).get(key);
				record.put(key, convertType(column, record.get(key)));
			}
		}
		for (String key : definition.get(table).keySet()) {
			if (definition.get(table).get(key).getPk() == true) {
				record.remove(key);
			}
		}
	}

	@Override
	public String create(String table, Record record) {
		if (definition.containsKey(table)) {
			sanitizeRecord(table, record);
			Table<?> t = DSL.table(DSL.name(table));
			ArrayList<Field<?>> columns = new ArrayList<>();
			ArrayList<Object> values = new ArrayList<>();
			for (String key : record.keySet()) {
				columns.add(DSL.field(key));
				values.add(record.get(key));
			}
			Field<?> pk = DSL.field(definition.get(table).getPk());
			return String.valueOf(dsl.insertInto(t).columns(columns).values(values).returning(pk).fetchOne());
		}
		return null;
	}

	@Override
	public Record read(String table, String id) {
		return null;
	}

	@Override
	public Integer update(String table, String id, Record record) {
		return 0;
	}

	@Override
	public Integer delete(String table, String id) {
		return 0;
	}

	@Override
	public ListResponse list(String table) {
		return null;
	}

	@Override
	public boolean updateDefinition() {
		return false;
	}

}
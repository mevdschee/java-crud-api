package com.tqdev.crudapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

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
			Object result = dsl.insertInto(t).columns(columns).values(values).returning(pk).fetchOne();
			if (result == null) {
				return null;
			}
			return String.valueOf(result);
		}
		return null;
	}

	@Override
	public Record read(String table, String id) {
		if (definition.containsKey(table)) {
			Table<?> t = DSL.table(DSL.name(table));
			ArrayList<Field<?>> columns = new ArrayList<>();
			for (String key : definition.get(table).keySet()) {
				columns.add(DSL.field(key));
			}
			Field<Object> pk = DSL.field(definition.get(table).getPk());
			return Record.valueOf(dsl.select(columns).from(t).where(pk.eq(id)).fetchOne());
		}
		return null;
	}

	@Override
	public Integer update(String table, String id, Record record) {
		if (definition.containsKey(table)) {
			sanitizeRecord(table, record);
			Table<?> t = DSL.table(DSL.name(table));
			LinkedHashMap<Field<?>, Object> columns = new LinkedHashMap<>();
			for (String key : record.keySet()) {
				columns.put(DSL.field(key), record.get(key));
			}
			Field<Object> pk = DSL.field(definition.get(table).getPk());
			return dsl.update(t).set(columns).where(pk.eq(id)).execute();
		}
		return null;
	}

	@Override
	public Integer delete(String table, String id) {
		if (definition.containsKey(table)) {
			Table<?> t = DSL.table(DSL.name(table));
			Field<Object> pk = DSL.field(definition.get(table).getPk());
			return dsl.deleteFrom(t).where(pk.eq(id)).execute();
		}
		return null;
	}

	@Override
	public ListResponse list(String table) {
		if (definition.containsKey(table)) {
			Table<?> t = DSL.table(DSL.name(table));
			ArrayList<Record> records = new ArrayList<>();
			for (org.jooq.Record record : dsl.selectFrom(t).fetch()) {
				records.add(Record.valueOf(record));
			}
			return new ListResponse(records.toArray(new Record[records.size()]));
		}
		return null;
	}

	@Override
	public boolean updateDefinition() {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource("columns.json");
		boolean result = true;
		try {
			definition = mapper.readValue(resource.getInputStream(), DatabaseDefinition.class);
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

}
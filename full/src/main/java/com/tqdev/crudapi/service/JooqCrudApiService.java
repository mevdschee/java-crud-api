package com.tqdev.crudapi.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.record.ListResponse;
import com.tqdev.crudapi.service.record.Record;

public class JooqCrudApiService extends BaseCrudApiService implements CrudApiService {

	private DSLContext dsl;

	public JooqCrudApiService(DSLContext dsl) {
		this.dsl = dsl;
		updateDefinition();
	}

	private void sanitizeRecord(String table, Record record) {
		for (String key : record.keySet()) {
			if (!definition.get(table).containsKey(key)) {
				record.remove(key);
			}
		}
		for (String key : definition.get(table).keySet()) {
			if (definition.get(table).get(key).getPk() == true) {
				record.remove(key);
			}
		}
	}

	@Override
	public String create(String table, Record record, Params params) {
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
	public Record read(String table, String id, Params params) {
		if (definition.containsKey(table)) {
			Table<?> t = DSL.table(DSL.name(table));
			ArrayList<Field<?>> columns = new ArrayList<>();
			for (String key : columns(table, params)) {
				columns.add(DSL.field(key));
			}
			Field<Object> pk = DSL.field(definition.get(table).getPk());
			return Record.valueOf(dsl.select(columns).from(t).where(pk.eq(id)).fetchOne());
		}
		return null;
	}

	@Override
	public Integer update(String table, String id, Record record, Params params) {
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
	public Integer delete(String table, String id, Params params) {
		if (definition.containsKey(table)) {
			Table<?> t = DSL.table(DSL.name(table));
			Field<Object> pk = DSL.field(definition.get(table).getPk());
			return dsl.deleteFrom(t).where(pk.eq(id)).execute();
		}
		return null;
	}

	@Override
	public ListResponse list(String table, Params params) {
		if (definition.containsKey(table)) {
			Table<?> t = DSL.table(DSL.name(table));
			ArrayList<Field<?>> columns = new ArrayList<>();
			for (String key : columns(table, params)) {
				columns.add(DSL.field(key));
			}
			ArrayList<Record> records = new ArrayList<>();
			for (org.jooq.Record record : dsl.select(columns).from(t).fetch()) {
				records.add(Record.valueOf(record));
			}
			return new ListResponse(records.toArray(new Record[records.size()]));
		}
		return null;
	}

	@Override
	public boolean updateDefinition() {
		DatabaseDefinition definition = DatabaseDefinition.fromValue(dsl);
		if (definition != null) {
			this.definition = definition;
			return true;
		}
		return false;
	}

}
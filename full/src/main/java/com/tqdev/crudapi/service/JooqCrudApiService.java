package com.tqdev.crudapi.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.record.ListResponse;
import com.tqdev.crudapi.service.record.Record;
import com.tqdev.crudapi.spatial.GeometryConverter;

public class JooqCrudApiService extends BaseCrudApiService implements CrudApiService {

	private DSLContext dsl;

	public JooqCrudApiService(DSLContext dsl) {
		this.dsl = dsl;
		updateDefinition();
	}

	@Override
	public String create(String table, Record record, Params params) {
		if (definition.containsKey(table)) {
			sanitizeRecord(table, record);
			Table<?> t = DSL.table(DSL.name(table));
			ArrayList<Field<?>> columns = new ArrayList<>();
			ArrayList<Object> values = new ArrayList<>();
			for (String key : record.keySet()) {
				if (definition.get(table).get(key).getType() == "geometry") {
					columns.add(DSL.field(key, new GeometryConverter()));
				} else {
					columns.add(DSL.field(key));
				}
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
			return Record.valueOf(dsl.select(columns).from(t).where(pk.eq(id)).fetchOne().intoMap());
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
			for (org.jooq.Record record : dsl.select(columns).from(t).where(conditions(params)).fetch()) {
				records.add(Record.valueOf(record.intoMap()));
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

	private ArrayList<Condition> conditions(Params params) {
		ArrayList<Condition> conditions = new ArrayList<>();
		if (params.containsKey("filter")) {
			for (String key : params.get("filter")) {
				String[] parts2;
				String[] parts = key.split(",", 3);
				if (parts.length >= 2) {
					Condition condition = null;
					String command = parts[1];
					Boolean negate = false;
					Boolean spatial = false;
					if (command.length() > 2) {
						if (command.charAt(0) == 'n') {
							negate = true;
							command = command.substring(1);
						}
						if (command.charAt(0) == 's') {
							spatial = true;
							command = command.substring(1);
						}
					}
					if (parts.length == 3 || (parts.length == 2
							&& (command.equals("ic") || command.equals("is") || command.equals("iv")))) {
						if (spatial) {
							// TODO:Implement spatial
						} else {
							switch (command) {
							case "cs":
								condition = DSL.field(parts[0]).contains(parts[2]);
								break;
							case "sw":
								condition = DSL.field(parts[0]).startsWith(parts[2]);
								break;
							case "ew":
								condition = DSL.field(parts[0]).endsWith(parts[2]);
								break;
							case "eq":
								condition = DSL.field(parts[0]).eq(parts[2]);
								break;
							case "lt":
								condition = DSL.field(parts[0]).lt(parts[2]);
								break;
							case "le":
								condition = DSL.field(parts[0]).le(parts[2]);
								break;
							case "ge":
								condition = DSL.field(parts[0]).ge(parts[2]);
								break;
							case "gt":
								condition = DSL.field(parts[0]).gt(parts[2]);
								break;
							case "bt":
								parts2 = parts[2].split(",", 2);
								condition = DSL.field(parts[0]).between(parts2[0], parts2[1]);
								break;
							case "in":
								parts2 = parts[2].split(",");
								condition = DSL.field(parts[0]).in((Object[]) parts2);
								break;
							case "is":
								condition = DSL.field(parts[0]).isNull();
								break;
							}
						}
					}
					if (condition != null) {
						if (negate) {
							condition = condition.not();
						}
						conditions.add(condition);
					}
				}
			}
		}
		return conditions;
	}

}
package com.tqdev.crudapi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.core.record.DatabaseRecords;
import com.tqdev.crudapi.core.record.DatabaseRecordsException;
import com.tqdev.crudapi.core.record.ListResponse;
import com.tqdev.crudapi.core.record.Record;
import com.tqdev.crudapi.meta.CrudMetaService;
import com.tqdev.crudapi.meta.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.meta.reflection.ReflectedTable;
import com.tqdev.crudapi.spatial.SpatialDSL;

public class JooqCrudApiService extends BaseCrudApiService
		implements CrudApiService, JooqConditions, JooqColumnSelector, JooqOrdering, JooqPagination {

	public static final Logger logger = LoggerFactory.getLogger(JooqCrudApiService.class);

	protected DSLContext dsl;

	public JooqCrudApiService(DSLContext dsl, CrudMetaService meta) {
		this.dsl = dsl;
		this.tables = meta.getDatabaseReflection();
		SpatialDSL.registerDataTypes(dsl);
		update();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String create(String table, Record record, Params params) {
		sanitizeRecord(table, record);
		ReflectedTable t = tables.get(table);
		LinkedHashMap<Field<?>, Object> columns = columnValues(t, record, params);
		Field<Object> pk = tables.get(table).getPk();
		org.jooq.Record result = dsl.insertInto(t).set(columns).returning(pk).fetchOne();
		return result == null ? null : String.valueOf(result.get(0));
	}

	@Override
	public Record read(String table, String id, Params params) {
		ReflectedTable t = tables.get(table);
		ArrayList<Field<?>> columns = columnNames(t, params);
		Field<Object> pk = tables.get(table).getPk();
		org.jooq.Record record = dsl.select(columns).from(t).where(pk.eq(id)).fetchOne();
		return record == null ? null : Record.valueOf(record.intoMap());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer update(String table, String id, Record record, Params params) {
		sanitizeRecord(table, record);
		ReflectedTable t = tables.get(table);
		LinkedHashMap<Field<?>, Object> columns = columnValues(t, record, params);
		Field<Object> pk = tables.get(table).getPk();
		return dsl.update(t).set(columns).where(pk.eq(id)).execute();
	}

	@Override
	public Integer delete(String table, String id, Params params) {
		Table<?> t = tables.get(table);
		Field<Object> pk = tables.get(table).getPk();
		return dsl.deleteFrom(t).where(pk.eq(id)).execute();
	}

	@Override
	public ListResponse list(String table, Params params) {
		ArrayList<Record> records = new ArrayList<>();
		ReflectedTable t = tables.get(table);
		ArrayList<Field<?>> columns = columnNames(t, params);
		ArrayList<Condition> conditions = conditions(t, params);
		ArrayList<SortField<?>> ordering = ordering(t, params);
		int offset = offset(params);
		int numberOfRows = numberOfRows(params);
		for (org.jooq.Record record : dsl.select(columns).from(t).where(conditions).orderBy(ordering)
				.limit(offset, numberOfRows).fetch()) {
			records.add(Record.valueOf(record.intoMap()));
		}
		return new ListResponse(records.toArray(new Record[records.size()]));
	}

	@Override
	public void update() {
		tables.update(dsl);
	}

	@Override
	public void initialize(String recordsFilename) throws JsonParseException, JsonMappingException, IOException,
			DatabaseDefinitionException, DatabaseRecordsException {
		DatabaseRecords.fromFile(recordsFilename).create(this);
	}

}
package com.tqdev.crudapi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ResultQuery;
import org.jooq.SelectLimitStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.DSL;
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

public class JooqCrudApiService extends BaseCrudApiService
		implements CrudApiService, JooqConditions, JooqColumnSelector, JooqOrdering, JooqPagination, JooqIncluder {

	public static final Logger logger = LoggerFactory.getLogger(JooqCrudApiService.class);

	protected DSLContext dsl;

	public JooqCrudApiService(DSLContext dsl, CrudMetaService meta) {
		this.dsl = dsl;
		this.tables = meta.getDatabaseReflection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String create(String table, Record record, Params params) {
		sanitizeRecord(table, record, null);
		ReflectedTable t = tables.get(table);
		LinkedHashMap<Field<?>, Object> columns = columnValues(t, true, record, params);
		Field<Object> pk = tables.get(table).getPk();
		org.jooq.Record result = dsl.insertInto(t).set(columns).returning(pk).fetchOne();
		return String.valueOf(result.get(0));
	}

	@Override
	public Record read(String table, String id, Params params) {
		ReflectedTable t = tables.get(table);
		addMandatoryColumns(table, tables, params);
		ArrayList<Field<?>> columns = columnNames(t, true, params);
		Field<Object> pk = tables.get(table).getPk();
		org.jooq.Record record = dsl.select(columns).from(t).where(pk.eq(id)).fetchOne();
		if (record == null) {
			return null;
		}
		Record r = Record.valueOf(record.intoMap());
		addIncludes(table, r, tables, params, dsl);
		return r;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int update(String table, String id, Record record, Params params) {
		sanitizeRecord(table, record, id);
		ReflectedTable t = tables.get(table);
		LinkedHashMap<Field<?>, Object> columns = columnValues(t, true, record, params);
		Field<Object> pk = tables.get(table).getPk();
		return dsl.update(t).set(columns).where(pk.eq(id)).execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int increment(String table, String id, Record record, Params params) {
		sanitizeRecord(table, record, id);
		ReflectedTable t = tables.get(table);
		LinkedHashMap<Field<?>, Object> columns = columnValues(t, true, record, params);
		Field<Object> pk = tables.get(table).getPk();
		return dsl.update(t).set(columns).where(pk.eq(id)).execute();
	}

	@Override
	public int delete(String table, String id, Params params) {
		Table<?> t = tables.get(table);
		Field<Object> pk = tables.get(table).getPk();
		return dsl.deleteFrom(t).where(pk.eq(id)).execute();
	}

	@Override
	public ListResponse list(String table, Params params) {
		ArrayList<Record> records = new ArrayList<>();
		ReflectedTable t = tables.get(table);
		addMandatoryColumns(table, tables, params);
		ArrayList<Field<?>> columns = columnNames(t, true, params);
		ArrayList<Condition> conditions = conditions(t, params);
		ArrayList<SortField<?>> ordering = ordering(t, params);
		int count = 0;
		ResultQuery<org.jooq.Record> query;
		if (!hasPage(params)) {
			int size = resultSize(params);
			query = dsl.select(columns).from(t).where(conditions).orderBy(ordering);
			if (size != -1) {
				query = ((SelectLimitStep<org.jooq.Record>) query).limit(size);
			}
		} else {
			int offset = pageOffset(params);
			int limit = pageSize(params);
			count = (int) dsl.select(DSL.count()).from(t).where(conditions).fetchOne(0);
			query = dsl.select(columns).from(t).where(conditions).orderBy(ordering).limit(offset, limit);
		}
		for (org.jooq.Record record : query.fetch()) {
			records.add(Record.valueOf(record.intoMap()));
		}
		addIncludes(table, records, tables, params, dsl);
		return new ListResponse(records.toArray(new Record[records.size()]), count);
	}

	@Override
	public void update() {
		tables.update();
	}

	@Override
	public void initialize(String recordsFilename) throws JsonParseException, JsonMappingException, IOException,
			DatabaseDefinitionException, DatabaseRecordsException {
		DatabaseRecords.fromFile(recordsFilename).create(this);
	}

}
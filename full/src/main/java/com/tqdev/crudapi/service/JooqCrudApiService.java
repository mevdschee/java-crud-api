package com.tqdev.crudapi.service;

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
import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.definition.DatabaseDefinitionException;
import com.tqdev.crudapi.service.record.DatabaseRecords;
import com.tqdev.crudapi.service.record.DatabaseRecordsException;
import com.tqdev.crudapi.service.record.ListResponse;
import com.tqdev.crudapi.service.record.Record;
import com.tqdev.crudapi.spatial.SpatialDSL;

public class JooqCrudApiService extends BaseCrudApiService
		implements CrudApiService, JooqConditions, JooqColumnSelector, JooqOrdering, JooqPagination {

	public static final Logger logger = LoggerFactory.getLogger(JooqCrudApiService.class);

	private DSLContext dsl;

	public JooqCrudApiService(DSLContext dsl) {
		this.dsl = dsl;
		SpatialDSL.registerDataTypes(dsl);
		updateDefinition();
	}

	@Override
	public String create(String table, Record record, Params params) {
		sanitizeRecord(table, record);
		Table<?> t = definition.getTable(table);
		LinkedHashMap<Field<?>, Object> columns = columnValues(table, record, params, definition);
		Field<Object> pk = definition.getPkField(table);
		org.jooq.Record result = dsl.insertInto(t).set(columns).returning(pk).fetchOne();
		return result == null ? null : String.valueOf(result.get(0));
	}

	@Override
	public Record read(String table, String id, Params params) {
		Table<?> t = definition.getTable(table);
		ArrayList<Field<?>> columns = columnNames(table, params, definition);
		Field<Object> pk = definition.getPkField(table);
		org.jooq.Record record = dsl.select(columns).from(t).where(pk.eq(id)).fetchOne();
		return record == null ? null : Record.valueOf(record.intoMap());
	}

	@Override
	public Integer update(String table, String id, Record record, Params params) {
		sanitizeRecord(table, record);
		Table<?> t = definition.getTable(table);
		LinkedHashMap<Field<?>, Object> columns = columnValues(table, record, params, definition);
		Field<Object> pk = definition.getPkField(table);
		return dsl.update(t).set(columns).where(pk.eq(id)).execute();
	}

	@Override
	public Integer delete(String table, String id, Params params) {
		Table<?> t = definition.getTable(table);
		Field<Object> pk = definition.getPkField(table);
		return dsl.deleteFrom(t).where(pk.eq(id)).execute();
	}

	@Override
	public ListResponse list(String table, Params params) {
		ArrayList<Record> records = new ArrayList<>();
		Table<?> t = definition.getTable(table);
		ArrayList<Field<?>> columns = columnNames(table, params, definition);
		ArrayList<Condition> conditions = conditions(table, params, definition);
		ArrayList<SortField<?>> ordering = ordering(table, params, definition);
		int offset = offset(params);
		int numberOfRows = numberOfRows(params);
		for (org.jooq.Record record : dsl.select(columns).from(t).where(conditions).orderBy(ordering)
				.limit(offset, numberOfRows).fetch()) {
			records.add(Record.valueOf(record.intoMap()));
		}
		return new ListResponse(records.toArray(new Record[records.size()]));
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

	@Override
	public void initialize(String columnsFilename, String recordsFilename) throws JsonParseException,
			JsonMappingException, IOException, DatabaseDefinitionException, DatabaseRecordsException {
		DatabaseDefinition.fromFile(columnsFilename).create(dsl);
		updateDefinition();
		DatabaseRecords.fromFile(recordsFilename).create(this);
	}

}
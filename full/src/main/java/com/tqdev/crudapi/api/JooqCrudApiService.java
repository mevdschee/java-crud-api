package com.tqdev.crudapi.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.tqdev.crudapi.api.record.DatabaseRecords;
import com.tqdev.crudapi.api.record.DatabaseRecordsException;
import com.tqdev.crudapi.api.record.ListResponse;
import com.tqdev.crudapi.api.record.Record;
import com.tqdev.crudapi.meta.CrudMetaService;
import com.tqdev.crudapi.meta.reflection.ReflectedTable;

public class JooqCrudApiService extends BaseCrudApiService implements CrudApiService {

	public static final Logger logger = LoggerFactory.getLogger(JooqCrudApiService.class);

	private DSLContext dsl;

	private ColumnSelector columns;
	private RelationIncluder includer;
	private FilterInfo filters;
	private OrderingInfo ordering;
	private PaginationInfo pagination;

	public JooqCrudApiService(DSLContext dsl, CrudMetaService meta) {
		this.dsl = dsl;
		tables = meta.getDatabaseReflection();
		columns = new ColumnSelector();
		includer = new RelationIncluder(columns);
		filters = new FilterInfo();
		ordering = new OrderingInfo();
		pagination = new PaginationInfo();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String create(String tableName, Record record, Params params) {
		sanitizeRecord(tableName, record, null);
		ReflectedTable table = tables.get(tableName);
		LinkedHashMap<Field<?>, Object> columnValues = columns.getValues(table, true, record, params);
		Field<Object> pk = tables.get(tableName).getPk();
		org.jooq.Record result = dsl.insertInto(table).set(columnValues).returning(pk).fetchOne();
		return String.valueOf(result.get(0));
	}

	@Override
	public Record read(String tableName, String id, Params params) {
		ReflectedTable table = tables.get(tableName);
		includer.addMandatoryColumns(table, tables, params);
		ArrayList<Field<?>> columnNames = columns.getNames(table, true, params);
		Field<Object> pk = tables.get(tableName).getPk();
		org.jooq.Record record = dsl.select(columnNames).from(table).where(pk.eq(id)).fetchOne();
		if (record == null) {
			return null;
		}
		Record r = Record.valueOf(record.intoMap());
		ArrayList<Record> records = new ArrayList<>(Arrays.asList(r));
		includer.addIncludes(tableName, records, tables, params, dsl);
		return r;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int update(String tableName, String id, Record record, Params params) {
		sanitizeRecord(tableName, record, id);
		ReflectedTable table = tables.get(tableName);
		LinkedHashMap<Field<?>, Object> columnValues = columns.getValues(table, true, record, params);
		Field<Object> pk = tables.get(tableName).getPk();
		return dsl.update(table).set(columnValues).where(pk.eq(id)).execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int increment(String tableName, String id, Record record, Params params) {
		sanitizeRecord(tableName, record, id);
		ReflectedTable table = tables.get(tableName);
		LinkedHashMap<Field<?>, Object> columnValues = columns.getValues(table, true, record, params);
		Field<Object> pk = tables.get(tableName).getPk();
		return dsl.update(table).set(columnValues).where(pk.eq(id)).execute();
	}

	@Override
	public int delete(String tableName, String id, Params params) {
		Table<?> table = tables.get(tableName);
		Field<Object> pk = tables.get(tableName).getPk();
		return dsl.deleteFrom(table).where(pk.eq(id)).execute();
	}

	@Override
	public ListResponse list(String tableName, Params params) {
		ArrayList<Record> records = new ArrayList<>();
		ReflectedTable table = tables.get(tableName);
		includer.addMandatoryColumns(table, tables, params);
		ArrayList<Field<?>> columnNames = columns.getNames(table, true, params);
		Condition condition= filters.getCombinedConditions(table, params);
		ArrayList<SortField<?>> columnOrdering = ordering.getColumnOrdering(table, params);
		int count = 0;
		ResultQuery<org.jooq.Record> query;
		if (!pagination.hasPage(params)) {
			int size = pagination.getResultSize(params);
			query = dsl.select(columnNames).from(table).where(condition).orderBy(columnOrdering);
			if (size != -1) {
				query = ((SelectLimitStep<org.jooq.Record>) query).limit(size);
			}
		} else {
			int offset = pagination.getPageOffset(params);
			int limit = pagination.getPageSize(params);
			count = (int) dsl.select(DSL.count()).from(table).where(condition).fetchOne(0);
			query = dsl.select(columnNames).from(table).where(condition).orderBy(columnOrdering).limit(offset, limit);
		}
		for (org.jooq.Record record : query.fetch()) {
			records.add(Record.valueOf(record.intoMap()));
		}
		includer.addIncludes(tableName, records, tables, params, dsl);
		return new ListResponse(records.toArray(new Record[records.size()]), count);
	}

	@Override
	public void update() {
		tables.update();
	}

	@Override
	public void initialize(String recordsFilename) throws IOException, DatabaseRecordsException {
		DatabaseRecords.fromFile(recordsFilename).create(this);
	}

}
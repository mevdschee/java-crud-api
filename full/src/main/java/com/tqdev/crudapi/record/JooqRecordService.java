package com.tqdev.crudapi.record;

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

import com.tqdev.crudapi.column.ColumnService;
import com.tqdev.crudapi.column.reflection.ReflectedTable;
import com.tqdev.crudapi.record.container.DatabaseRecords;
import com.tqdev.crudapi.record.container.DatabaseRecordsException;
import com.tqdev.crudapi.record.container.Record;
import com.tqdev.crudapi.record.document.ListDocument;

public class JooqRecordService extends BaseRecordService implements RecordService {

	public static final Logger logger = LoggerFactory.getLogger(JooqRecordService.class);

	private DSLContext dsl;

	private ColumnSelector columns;
	private RelationIncluder includer;
	private FilterInfo filters;
	private OrderingInfo ordering;
	private PaginationInfo pagination;

	public JooqRecordService(DSLContext dsl, ColumnService columns) {
		this.dsl = dsl;
		reflection = columns.getDatabaseReflection();
		this.columns = new ColumnSelector();
		includer = new RelationIncluder(this.columns);
		filters = new FilterInfo();
		ordering = new OrderingInfo();
		pagination = new PaginationInfo();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String create(String tableName, Record record, Params params) {
		sanitizeRecord(tableName, record, null);
		ReflectedTable table = reflection.getTable(tableName);
		LinkedHashMap<Field<?>, Object> columnValues = columns.getValues(table, true, record, params);
		Field<Object> pk = reflection.getTable(tableName).getPk();
		org.jooq.Record result = dsl.insertInto(table).set(columnValues).returning(pk).fetchOne();
		if (result==null) {
			return String.valueOf(columnValues.get(pk));
		}
		return String.valueOf(result.get(0));
	}

	@Override
	public Record read(String tableName, String id, Params params) {
		ReflectedTable table = reflection.getTable(tableName);
		includer.addMandatoryColumns(table, reflection, params);
		ArrayList<Field<?>> columnNames = columns.getNames(table, true, params);
		Field<Object> pk = reflection.getTable(tableName).getPk();
		org.jooq.Record record = dsl.select(columnNames).from(table).where(pk.eq(id)).fetchOne();
		if (record == null) {
			return null;
		}
		Record r = Record.valueOf(record.intoMap());
		ArrayList<Record> records = new ArrayList<>(Arrays.asList(r));
		includer.addIncludes(tableName, records, reflection, params, dsl);
		return r;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int update(String tableName, String id, Record record, Params params) {
		sanitizeRecord(tableName, record, id);
		ReflectedTable table = reflection.getTable(tableName);
		LinkedHashMap<Field<?>, Object> columnValues = columns.getValues(table, true, record, params);
		Field<Object> pk = reflection.getTable(tableName).getPk();
		return dsl.update(table).set(columnValues).where(pk.eq(id)).execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int increment(String tableName, String id, Record record, Params params) {
		sanitizeRecord(tableName, record, id);
		ReflectedTable table = reflection.getTable(tableName);
		LinkedHashMap<Field<?>, Object> columnValues = columns.getValues(table, true, record, params);
		Field<Object> pk = reflection.getTable(tableName).getPk();
		return dsl.update(table).set(columnValues).where(pk.eq(id)).execute();
	}

	@Override
	public int delete(String tableName, String id, Params params) {
		Table<?> table = reflection.getTable(tableName);
		Field<Object> pk = reflection.getTable(tableName).getPk();
		return dsl.deleteFrom(table).where(pk.eq(id)).execute();
	}

	@Override
	public ListDocument list(String tableName, Params params) {
		ArrayList<Record> records = new ArrayList<>();
		ReflectedTable table = reflection.getTable(tableName);
		includer.addMandatoryColumns(table, reflection, params);
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
		includer.addIncludes(tableName, records, reflection, params, dsl);
		return new ListDocument(records.toArray(new Record[records.size()]), count);
	}

	@Override
	public void update() {
		reflection.update();
	}

	@Override
	public void initialize(String recordsFilename) throws IOException, DatabaseRecordsException {
		DatabaseRecords.fromFile(recordsFilename).create(this);
	}

}
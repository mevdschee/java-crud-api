package com.tqdev.crudapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tqdev.crudapi.service.definition.ColumnDefinition;
import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.record.DatabaseRecords;
import com.tqdev.crudapi.service.record.ListResponse;
import com.tqdev.crudapi.service.record.MemoryRecord;
import com.tqdev.crudapi.service.record.Record;

public class MemoryCrudApiService extends BaseCrudApiService
		implements CrudApiService, ColumnSelector, MemoryConditions {

	private ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, ConcurrentHashMap<String, MemoryRecord>> database = new ConcurrentHashMap<>();

	private String columnsFilename;
	private String recordsFilename;

	public MemoryCrudApiService(String columnsFilename, String recordsFilename)
			throws JsonParseException, JsonMappingException, IOException {
		this.columnsFilename = columnsFilename;
		this.recordsFilename = recordsFilename;
		updateDefinition();
	}

	private MemoryRecord typeRecord(String table, String id, Record record) {
		MemoryRecord r = new MemoryRecord();
		for (String key : definition.get(table).keySet()) {
			ColumnDefinition column = definition.get(table).get(key);
			if (definition.get(table).get(key).getPk() == true) {
				r.putTyped(key, id, column);
			} else if (record.containsKey(key)) {
				r.putTyped(key, record.get(key), column);
			} else {
				r.putTyped(key, null, column);
			}
		}
		return r;
	}

	@Override
	public String create(String table, Record record, Params params) {
		if (database.containsKey(table)) {
			String id = String.valueOf(counters.get(table).incrementAndGet());
			sanitizeRecord(table, record);
			database.get(table).put(id, typeRecord(table, id, record));
			return id;
		}
		return null;
	}

	@Override
	public Record read(String table, String id, Params params) {
		if (database.containsKey(table)) {
			if (database.get(table).containsKey(id)) {
				MemoryRecord record = database.get(table).get(id);
				LinkedHashSet<String> columns = new LinkedHashSet<>();
				for (String key : columns(table, params, definition)) {
					columns.add(key);
				}
				return record.selectColumns(columns);
			}
		}
		return null;
	}

	@Override
	public Integer update(String table, String id, Record record, Params params) {
		if (database.containsKey(table)) {
			sanitizeRecord(table, record);
			database.get(table).put(id, typeRecord(table, id, record));
			return 1;
		}
		return 0;
	}

	@Override
	public Integer delete(String table, String id, Params params) {
		if (database.containsKey(table) && database.get(table).containsKey(id)) {
			database.get(table).remove(id);
			return 1;
		}
		return 0;
	}

	@Override
	public ListResponse list(String table, Params params) {
		if (database.containsKey(table)) {
			LinkedHashSet<String> columns = new LinkedHashSet<>();
			for (String key : columns(table, params, definition)) {
				columns.add(key);
			}
			ArrayList<Record> records = new ArrayList<>();
			for (MemoryRecord record : database.get(table).values()) {
				if (matchesConditions(record, params)) {
					records.add(record.selectColumns(columns));
				}
			}
			return new ListResponse(records.toArray(new Record[] {}));
		}
		return null;
	}

	@Override
	public boolean updateDefinition() {
		try {
			this.definition = DatabaseDefinition.fromFile(columnsFilename);
			counters = new ConcurrentHashMap<>();
			database = new ConcurrentHashMap<>();
			applyDefinition();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void applyDefinition() throws JsonParseException, JsonMappingException, IOException {
		for (String table : definition.keySet()) {
			counters.put(table, new AtomicLong());
			database.put(table, new ConcurrentHashMap<String, MemoryRecord>());
		}
		DatabaseRecords.fromFile(recordsFilename).create(this);
	}

}

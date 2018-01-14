package com.tqdev.crudapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryCrudApiService extends BaseCrudApiService implements CrudApiService {

	private ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, ConcurrentHashMap<String, Record>> database = new ConcurrentHashMap<>();

	private String columnsFilename;
	private String recordsFilename;

	public MemoryCrudApiService(String columnsFilename, String recordsFilename) {
		this.columnsFilename = columnsFilename;
		this.recordsFilename = recordsFilename;
		updateDefinition();
	}

	private void sanitizeRecord(String table, String id, Record record) {
		for (String key : record.keySet()) {
			if (!definition.get(table).containsKey(key)) {
				record.remove(key);
			} else {
				ColumnDefinition column = definition.get(table).get(key);
				record.putTyped(key, record.get(key), column);
			}
		}
		for (String key : definition.get(table).keySet()) {
			ColumnDefinition column = definition.get(table).get(key);
			if (!record.containsKey(key)) {
				record.putTyped(key, null, column);
			}
			if (definition.get(table).get(key).getPk() == true) {
				record.putTyped(key, id, column);
			}
		}
	}

	@Override
	public String create(String table, Record record, Params params) {
		if (database.containsKey(table)) {
			String id = String.valueOf(counters.get(table).incrementAndGet());
			sanitizeRecord(table, id, record);
			database.get(table).put(id, record);
			return id;
		}
		return null;
	}

	@Override
	public Record read(String table, String id, Params params) {
		if (database.containsKey(table)) {
			if (database.get(table).containsKey(id)) {
				Record record = database.get(table).get(id);
				LinkedHashSet<String> columns = new LinkedHashSet<>();
				for (String key : columns(table, params)) {
					columns.add(key);
				}
				return record.copyColumns(columns);
			}
		}
		return null;
	}

	@Override
	public Integer update(String table, String id, Record record, Params params) {
		if (database.containsKey(table)) {
			sanitizeRecord(table, id, record);
			database.get(table).put(id, record);
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
			for (String key : columns(table, params)) {
				columns.add(key);
			}
			ArrayList<Record> records = new ArrayList<>();
			for (Record record : database.get(table).values()) {
				records.add(record.copyColumns(columns));
			}
			return new ListResponse(records.toArray(new Record[] {}));
		}
		return null;
	}

	@Override
	public boolean updateDefinition() {
		DatabaseDefinition definition = DatabaseDefinition.fromValue(columnsFilename);
		if (definition != null) {
			this.definition = definition;
			counters = new ConcurrentHashMap<>();
			database = new ConcurrentHashMap<>();
			return applyDefinition();
		}
		return false;
	}

	private boolean applyDefinition() {
		for (String table : definition.keySet()) {
			counters.put(table, new AtomicLong());
			database.put(table, new ConcurrentHashMap<String, Record>());
		}
		try {
			DatabaseRecords.fromFile(recordsFilename).create(this);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}

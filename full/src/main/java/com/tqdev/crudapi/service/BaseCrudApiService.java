package com.tqdev.crudapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.record.DatabaseRecords;
import com.tqdev.crudapi.service.record.Record;

abstract class BaseCrudApiService implements CrudApiService {

	protected DatabaseDefinition definition = new DatabaseDefinition();

	protected void sanitizeRecord(String table, Record record) {
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

	protected Set<String> columns(String table, Params params) {
		if (!params.containsKey("columns")) {
			return definition.get(table).keySet();
		}
		HashMap<String, Boolean> columns = new HashMap<>();
		for (String key : params.get("columns").get(0).split(",")) {
			columns.put(key, true);
		}
		LinkedHashSet<String> result = new LinkedHashSet<>();
		for (String key : definition.get(table).keySet()) {
			if (columns.containsKey("*.*") || columns.containsKey(table + ".*")
					|| columns.containsKey(table + "." + key) || columns.containsKey("*") || columns.containsKey(key)) {
				result.add(key);
			}
		}
		return result;
	}

	@Override
	public DatabaseDefinition getDatabaseDefinition() {
		return definition;
	}

	@Override
	public DatabaseRecords getDatabaseRecords() {
		DatabaseRecords db = new DatabaseRecords();
		for (String table : definition.keySet()) {
			ArrayList<Record> records = new ArrayList<>();
			for (Record record : list(table, new Params()).getRecords()) {
				records.add(record);
			}
			db.put(table, records);
		}
		return db;
	}

}

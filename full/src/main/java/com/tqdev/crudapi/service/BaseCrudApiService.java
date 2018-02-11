package com.tqdev.crudapi.service;

import java.util.ArrayList;

import com.tqdev.crudapi.service.definition.DatabaseDefinition;
import com.tqdev.crudapi.service.record.DatabaseRecords;
import com.tqdev.crudapi.service.record.Record;

abstract class BaseCrudApiService implements CrudApiService {

	protected DatabaseDefinition definition = new DatabaseDefinition();

	protected void sanitizeRecord(String table, Record record) {
		String[] keyset = record.keySet().toArray(new String[] {});
		for (String key : keyset) {
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

package com.tqdev.crudapi.service;

import java.util.ArrayList;

import org.jooq.Field;

import com.tqdev.crudapi.reflection.DatabaseReflection;
import com.tqdev.crudapi.service.record.DatabaseRecords;
import com.tqdev.crudapi.service.record.Record;

abstract class BaseCrudApiService implements CrudApiService {

	protected DatabaseReflection tables = new DatabaseReflection();

	protected void sanitizeRecord(String table, Record record) {
		String[] keyset = record.keySet().toArray(new String[] {});
		for (String key : keyset) {
			if (!tables.get(table).exists(key)) {
				record.remove(key);
			}
		}
		Field<?> pk = tables.get(table).getPk();
		for (String key : tables.get(table).fieldNames()) {
			Field<?> field = tables.get(table).get(key);
			if (field.getName().equals(pk.getName())) {
				record.remove(key);
			}
		}
	}

	@Override
	public DatabaseRecords getDatabaseRecords() {
		DatabaseRecords db = new DatabaseRecords();
		for (String table : tables.tableNames()) {
			ArrayList<Record> records = new ArrayList<>();
			for (Record record : list(table, new Params()).getRecords()) {
				records.add(record);
			}
			db.put(table, records);
		}
		return db;
	}

	@Override
	public boolean exists(String table) {
		return tables.get(table) != null;
	}

}

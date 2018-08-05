package com.tqdev.crudapi.record;

import java.util.ArrayList;

import org.jooq.Field;

import com.tqdev.crudapi.column.reflection.DatabaseReflection;
import com.tqdev.crudapi.record.container.DatabaseRecords;
import com.tqdev.crudapi.record.container.Record;

abstract class BaseRecordService implements RecordService {

	protected DatabaseReflection tables;

	protected void sanitizeRecord(String table, Record record, String id) {
		String[] keyset = record.keySet().toArray(new String[] {});
		for (String key : keyset) {
			if (!tables.get(table).exists(key)) {
				record.remove(key);
			}
		}
		if (id != null) {
			Field<?> pk = tables.get(table).getPk();
			for (String key : tables.get(table).fieldNames()) {
				Field<?> field = tables.get(table).get(key);
				if (field.getName().equals(pk.getName())) {
					record.remove(key);
				}
			}
		}
	}

	@Override
	public boolean exists(String table) {
		return tables.exists(table);
	}

	@Override
	public DatabaseRecords getDatabaseRecords() {
		DatabaseRecords db = new DatabaseRecords();
		for (String table : tables.getTableNames()) {
			ArrayList<Record> records = new ArrayList<>();
			for (Record record : list(table, new Params()).getRecords()) {
				records.add(record);
			}
			db.put(table, records);
		}
		return db;
	}

}

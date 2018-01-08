package com.tqdev.crudapi.service;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MemoryCrudApiService implements CrudApiService {

	private ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, ConcurrentHashMap<String, Record>> database = new ConcurrentHashMap<>();

	private DatabaseDefinition definition = new DatabaseDefinition();

	private String filename;

	public MemoryCrudApiService(String filename) {
		this.filename = filename;
		updateDefinition();
	}

	private Object getDefaultValue(ColumnDefinition column) {
		if (column.getNullable() == true) {
			return null;
		}
		switch (column.getType()) {
		case "string":
			return new String("");
		case "integer":
			return new Integer(0);
		case "datetime":
			return new String("1970-01-01T00:00:00Z");
		case "boolean":
			return new Boolean(false);
		case "decimal":
			return new String("0");
		}
		return new String("");
	}

	private Object convertType(ColumnDefinition column, Object value) {
		if (value == null) {
			if (column.getNullable() == true) {
				return null;
			}
			return getDefaultValue(column);
		}
		switch (column.getType()) {
		case "string":
			if (!(value instanceof String)) {
				return String.valueOf(value);
			}
			break;
		case "integer":
			if (value instanceof String) {
				return Integer.parseInt((String) value);
			}
			break;
		}
		return value;
	}

	private void sanitizeRecord(String table, String id, Record record) {
		for (String key : record.keySet()) {
			if (!definition.get(table).containsKey(key)) {
				record.remove(key);
			} else {
				ColumnDefinition column = definition.get(table).get(key);
				record.put(key, convertType(column, record.get(key)));
			}
		}
		for (String key : definition.get(table).keySet()) {
			ColumnDefinition column = definition.get(table).get(key);
			if (!record.containsKey(key)) {
				record.put(key, getDefaultValue(column));
			}
			if (definition.get(table).get(key).getPk() == true) {
				record.put(key, convertType(column, id));
			}
		}
	}

	@Override
	public String create(String table, Record record) {
		if (database.containsKey(table)) {
			String id = String.valueOf(counters.get(table).incrementAndGet());
			sanitizeRecord(table, id, record);
			database.get(table).put(id, record);
			return id;
		}
		return null;
	}

	@Override
	public Record read(String table, String id) {
		if (database.containsKey(table)) {
			if (database.get(table).containsKey(id)) {
				return Record.valueOf(database.get(table).get(id));
			}
		}
		return null;
	}

	@Override
	public Integer update(String table, String id, Record record) {
		if (database.containsKey(table)) {
			sanitizeRecord(table, id, record);
			database.get(table).put(id, record);
			return 1;
		}
		return 0;
	}

	@Override
	public Integer delete(String table, String id) {
		if (database.containsKey(table) && database.get(table).containsKey(id)) {
			database.get(table).remove(id);
			return 1;
		}
		return 0;
	}

	@Override
	public ListResponse list(String table) {
		if (database.containsKey(table)) {
			ListResponse result = new ListResponse();
			result.records = database.get(table).values().toArray(new Record[] {});
			return result;
		}
		return null;
	}

	@Override
	public boolean updateDefinition() {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource(filename);
		boolean result = true;
		try {
			applyDefinition(mapper.readValue(resource.getInputStream(), DatabaseDefinition.class));
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

	private void applyDefinition(DatabaseDefinition definition) {
		for (String table : definition.keySet()) {
			if (!database.containsKey(table)) {
				ConcurrentHashMap<String, Record> records = new ConcurrentHashMap<>();
				counters.put(table, new AtomicLong());
				database.put(table, records);
			}
		}
		this.definition = definition;
		for (String table : database.keySet()) {
			if (!definition.containsKey(table)) {
				database.remove(table);
				counters.remove(table);
			}
		}
	}

}

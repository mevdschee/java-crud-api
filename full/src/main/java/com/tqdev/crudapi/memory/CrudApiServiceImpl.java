package com.tqdev.crudapi.memory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.service.ColumnDefinition;
import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.DatabaseDefinition;
import com.tqdev.crudapi.service.ListResponse;
import com.tqdev.crudapi.service.Record;
import com.tqdev.crudapi.service.TableDefinition;

public class CrudApiServiceImpl implements CrudApiService {

	private ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, ConcurrentHashMap<String, Record>> database = new ConcurrentHashMap<>();

	private DatabaseDefinition definition = new DatabaseDefinition();

	public CrudApiServiceImpl(String filename) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource(filename);
		DatabaseDefinition info = mapper.readValue(resource.getInputStream(), DatabaseDefinition.class);
		for (String table : info.keySet()) {
			createTable(table, info.get(table));
		}
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
	public boolean dropTable(String table) {
		if (database.containsKey(table)) {
			counters.remove(table);
			database.remove(table);
			return true;
		}
		return false;
	}

	@Override
	public boolean createTable(String table, TableDefinition tableDefinition) {
		if (!database.containsKey(table)) {
			ConcurrentHashMap<String, Record> records = new ConcurrentHashMap<>();
			counters.put(table, new AtomicLong());
			database.put(table, records);
			definition.put(table, tableDefinition);
			return true;
		}
		return false;
	}

}

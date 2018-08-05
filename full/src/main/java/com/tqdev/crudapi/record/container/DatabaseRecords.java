package com.tqdev.crudapi.record.container;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.record.RecordService;

public class DatabaseRecords {

	private LinkedHashMap<String, TableRecords> tables = new LinkedHashMap<>();

	public Collection<TableRecords> getTables() {
		return tables.values();
	}

	public void setTables(Collection<TableRecords> tables) {
		this.tables = new LinkedHashMap<>();
		for (TableRecords table : tables) {
			this.tables.put(table.getName(), table);
		}
	}

	public static DatabaseRecords fromFile(String filename)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource(filename);
		DatabaseRecords result;
		try {
			result = mapper.readValue(resource.getInputStream(), DatabaseRecords.class);
		} catch (FileNotFoundException e) {
			result = new DatabaseRecords();
		}
		return result;
	}

	public void create(RecordService service) throws DatabaseRecordsException {
		for (String table : tables.keySet()) {
			tables.get(table).create(service);
		}
	}

	public void put(String table, ArrayList<Record> records) {
		tables.put(table, new TableRecords(table, records));
	}
}

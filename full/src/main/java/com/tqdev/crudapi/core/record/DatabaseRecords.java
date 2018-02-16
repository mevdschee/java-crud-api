package com.tqdev.crudapi.core.record;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.core.CrudApiService;
import com.tqdev.crudapi.core.Params;

public class DatabaseRecords extends LinkedHashMap<String, ArrayList<Record>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	public void create(CrudApiService service) throws DatabaseRecordsException {
		for (String table : keySet()) {
			for (Record record : get(table)) {
				if (!service.exists(table)) {
					throw new DatabaseRecordsException(
							String.format("Cannot insert into table '%s': Table does not exist", table));
				}
				service.create(table, record, new Params());
			}
		}
	}
}

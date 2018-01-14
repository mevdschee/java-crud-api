package com.tqdev.crudapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.Params;
import com.tqdev.crudapi.service.Record;

public class DatabaseRecords extends HashMap<String, ArrayList<Record>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static DatabaseRecords fromFile(String filename)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource(filename);
		return mapper.readValue(resource.getInputStream(), DatabaseRecords.class);
	}

	public void create(CrudApiService service) {
		for (String table : keySet()) {
			if (service.list(table, new Params()).getRecords().length > 0) {
				continue;
			}
			for (Record record : get(table)) {
				service.create(table, record, new Params());
			}
		}
	}
}

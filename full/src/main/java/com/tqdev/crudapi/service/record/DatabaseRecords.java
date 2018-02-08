package com.tqdev.crudapi.service.record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jooq.DSLContext;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.Params;

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
			for (Record record : get(table)) {
				service.create(table, record, new Params());
			}
		}
	}

	public void create(DSLContext dsl) throws DatabaseRecordsException {

	}
}

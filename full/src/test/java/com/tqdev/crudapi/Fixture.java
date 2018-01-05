package com.tqdev.crudapi;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.Record;

@Component
public class Fixture {

	public void create(CrudApiService service) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource("records.json");
		DatabaseRecords records = mapper.readValue(resource.getInputStream(), DatabaseRecords.class);
		for (String table : records.keySet()) {
			for (Record record : records.get(table)) {
				service.create(table, record);
			}
		}
	}

}

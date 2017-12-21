package com.tqdev.crudapi;

import java.io.IOException;
import java.util.Iterator;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.Record;

@Component
public class Fixture {

	private JsonNode readJsonFile(String filename) throws IOException {
		ClassPathResource res = new ClassPathResource(filename);
		byte[] json = FileCopyUtils.copyToByteArray(res.getInputStream());
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getFactory();
		JsonParser jp = factory.createParser(json);
		return mapper.readTree(jp);
	}

	private void createTable(CrudApiService service, String table, JsonNode records) {
		service.dropTable(table);
		service.createTable(table, null);
		for (int i = 0; i < records.size(); i++) {
			createRecord(service, table, records.get(i));
		}
	}

	private void createRecord(CrudApiService service, String table, JsonNode fields) {
		Record record = new Record();
		Iterator<String> fieldNames = fields.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			record.put(fieldName, fields.get(fieldName));
		}
		service.create(table, record);
	}

	public void create(CrudApiService service) throws IOException {
		JsonNode json = readJsonFile("data.json");
		Iterator<String> tables = json.get("records").fieldNames();
		while (tables.hasNext()) {
			String table = tables.next();
			createTable(service, table, json.get("records").get(table));
		}
	}

}

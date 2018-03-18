package com.tqdev.crudapi.meta.openapi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tqdev.crudapi.meta.definition.DatabaseDefinition;
import com.tqdev.crudapi.meta.definition.TableDefinition;

public class SimpleOpenApiDefinition {

	protected ObjectNode root;

	@JsonAnyGetter
	public Map<String, JsonNode> any() {
		LinkedHashMap<String, JsonNode> result = new LinkedHashMap<>();
		Iterator<Entry<String, JsonNode>> fields = root.fields();
		while (fields.hasNext()) {
			Entry<String, JsonNode> field = fields.next();
			result.put(field.getKey(), field.getValue());
		}
		return result;
	}

	@JsonAnySetter
	public void set(String name, JsonNode value) {
		root.set(name, value);
	}

	public SimpleOpenApiDefinition() {
		ObjectMapper mapper = new ObjectMapper();
		root = mapper.createObjectNode();
	}

	public SimpleOpenApiDefinition(SimpleOpenApiDefinition copy) {
		root = copy.root.deepCopy();
	}

	public void inject(DatabaseDefinition database) {
		for (TableDefinition table : database.getTables()) {
			String path = "/data/" + table.getName();
			ObjectNode pathItem = root.with("paths").with(path);
			for (String method : new String[] { "get", "post", "put", "patch", "delete" }) {
				ObjectNode operation = pathItem.with(method);
				ObjectNode okResponse = operation.with("responses").with("200");
				okResponse.put("description", method + " operation");
			}
		}
	}

	public static SimpleOpenApiDefinition fromFile(String filename)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource(filename);
		SimpleOpenApiDefinition result;
		try {
			result = mapper.readValue(resource.getInputStream(), SimpleOpenApiDefinition.class);
		} catch (FileNotFoundException e) {
			result = null;
		}
		return result;
	}

}

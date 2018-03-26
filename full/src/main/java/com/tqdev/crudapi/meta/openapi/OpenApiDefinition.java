package com.tqdev.crudapi.meta.openapi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tqdev.crudapi.meta.definition.ColumnDefinition;
import com.tqdev.crudapi.meta.definition.DatabaseDefinition;
import com.tqdev.crudapi.meta.definition.TableDefinition;

public class OpenApiDefinition {

	protected ObjectNode root;

	public OpenApiDefinition() {
		ObjectMapper mapper = new ObjectMapper();
		root = mapper.createObjectNode();
	}

	public OpenApiDefinition(OpenApiDefinition copy) {
		root = copy.root.deepCopy();
	}

	public static OpenApiDefinition fromFile(String filename) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource(filename);
		OpenApiDefinition result;
		try {
			result = mapper.readValue(resource.getInputStream(), OpenApiDefinition.class);
		} catch (FileNotFoundException e) {
			result = null;
		}
		return result;
	}

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

	public void inject(DatabaseDefinition database) {
		for (TableDefinition table : database.getTables()) {
			String path = "/data/" + table.getName();
			ObjectNode paths = root.with("paths");
			ObjectNode pathItem = paths.with(path);
			for (String method : new String[] { "get", "post", "put", "patch", "delete" }) {
				ObjectNode operation = pathItem.with(method);
				ObjectNode okResponse = operation.with("responses").with("200");
				okResponse.put("description", method + " operation");
				fillContentResponse(table, okResponse);
				fillParametersWithPrimaryKey(method, table, paths);
			}
		}
	}

	private void fillParametersWithPrimaryKey(String method, TableDefinition table, ObjectNode paths) {
		if (table.getPk() != null) {
			String pathWithId = String.format("/data/%s/{%s}", table.getName(), table.getPk());
			ObjectNode pathItemId = paths.with(pathWithId);
			ObjectNode operation = pathItemId.with(method);

			ObjectNode okResponse = operation.with("responses").with("200");
			okResponse.put("description", method + " operation");

			fillResponseParameters(table, operation);
		}
	}

	private void fillResponseParameters(TableDefinition table, ObjectNode operation) {
		ArrayNode parameters = operation.putArray("parameters");

		// TODO: replace repeated objects with references
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("name", table.getPk().getName());
		node.put("in", "path");
		node.put("required", true);
		ObjectNode schema = node.with("schema");
		schema.put("type", convertTypeToJSONType(table.getPk().getName()));

		parameters.add(node);
	}

	private void fillContentResponse(TableDefinition table, ObjectNode okResponse) {
		ObjectNode content = okResponse.with("content");
		ObjectNode json = content.with("application/json");
		ObjectNode schema = json.with("schema");
		schema.put("type", "object");
		ObjectNode properties = schema.with("properties");

		Collection<ColumnDefinition> columns = table.getColumns();
		for (ColumnDefinition columnDefinition : columns) {
			ObjectNode col = properties.with(columnDefinition.getName());
			col.put("type", convertTypeToJSONType(columnDefinition.getType()));
		}

		ObjectNode example = schema.with("example");
		for (ColumnDefinition columnDefinition : columns) {
			String decodeType = convertTypeToJSONType(columnDefinition.getType());
			example.put(columnDefinition.getName(), getExampleForJSONType(decodeType));
		}
	}

	private String convertTypeToJSONType(String type) {
		String jsonType = "string";
		switch (type.split(" ")[0]) {
		case "varchar":
		case "char":
		case "longvarchar":
		case "clob":
			jsonType = "string";
			break;
		case "nvarchar":
		case "nchar":
		case "longnvarchar":
		case "nclob":
			jsonType = "string";
			break;
		case "boolean":
		case "bit":
			jsonType = "boolean";
		case "tinyint":
		case "smallint":
		case "integer":
		case "bigint":
			jsonType = "integer";
			break;
		case "double":
		case "float":
		case "real":
			jsonType = "float";
			break;
		case "numeric":
		case "decimal":
			jsonType = "float";
			break;
		case "date":
		case "time":
		case "timestamp":
			jsonType = "string";
			break;
		case "binary":
		case "varbinary":
		case "longvarbinary":
		case "blob":
			jsonType = "string";
			break;
		default:
			jsonType = "?";
		}
		return jsonType;
	}

	private String getExampleForJSONType(String jsonType) {
		String example;
		switch (jsonType) {
		case "string":
			example = "some text";
			break;
		case "integer":
			example = "1";
			break;
		case "boolean":
			example = "true";
			break;
		case "array":
			example = "[]";
		default:
			example = "?";
		}
		return example;
	}

}

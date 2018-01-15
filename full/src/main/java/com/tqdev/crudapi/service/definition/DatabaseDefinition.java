package com.tqdev.crudapi.service.definition;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatabaseDefinition extends HashMap<String, TableDefinition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String findTablePrefix(DSLContext dsl) {
		Connection connection = dsl.configuration().connectionProvider().acquire();
		String catalog = null, schema = null;
		try {
			catalog = connection.getCatalog();
			schema = connection.getSchema();
		} catch (SQLException e) {
			// error on table prefix
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				// ignore
			}
		}
		String prefix = "";
		if (catalog != null) {
			prefix += "\"" + catalog + "\".";
		}
		if (schema != null) {
			prefix += "\"" + schema + "\".";
		}
		return prefix;
	}

	public static DatabaseDefinition fromValue(DSLContext dsl) {
		DatabaseDefinition definition = new DatabaseDefinition();
		String prefix = findTablePrefix(dsl);
		for (Table<?> table : dsl.meta().getTables()) {
			if (!(prefix + "\"" + table.getName() + "\"").equals(table.toString())) {
				// table not in current catalog or schema
				continue;
			}
			definition.put(table.getName(), TableDefinition.fromValue(table));
		}
		return definition;
	}

	public static DatabaseDefinition fromValue(String filename)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource(filename);
		return mapper.readValue(resource.getInputStream(), DatabaseDefinition.class);
	}
}

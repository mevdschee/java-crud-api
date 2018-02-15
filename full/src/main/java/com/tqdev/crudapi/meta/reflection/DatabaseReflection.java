package com.tqdev.crudapi.meta.reflection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import org.jooq.DSLContext;
import org.jooq.Table;

public class DatabaseReflection {

	protected DSLContext dsl;

	protected HashMap<String, ReflectedTable> tables;

	public DatabaseReflection(DSLContext dsl) {
		this.dsl = dsl;
		update();
	}

	public boolean exists(String name) {
		return tables.containsKey(name);
	}

	public ReflectedTable get(String name) {
		return tables.get(name);
	}

	private String findTablePrefix() {
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

	public void update() {
		tables = new HashMap<>();
		String prefix = findTablePrefix();
		for (Table<?> table : dsl.meta().getTables()) {
			if (!(table.toString().startsWith(prefix))) {
				// table not in current catalog or schema
				continue;
			}
			tables.put(table.getName(), new ReflectedTable(table));
		}
	}

	public Set<String> tableNames() {
		return tables.keySet();
	}
}

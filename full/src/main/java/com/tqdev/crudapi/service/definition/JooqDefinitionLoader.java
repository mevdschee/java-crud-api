package com.tqdev.crudapi.service.definition;

import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class JooqDefinitionLoader {

	static public void load(DSLContext dsl, DatabaseDefinition definition) {
		for (String tableName : definition.keySet()) {
			ArrayList<Field<?>> fields = new ArrayList<>();
			TableDefinition columns = definition.get(tableName);
			for (String columnName : columns.keySet()) {
				fields.add(DSL.field(columnName, getDataType(columns.get(columnName))));
			}
			dsl.createTable(tableName).columns(fields).execute();
		}
	}

	static private DataType<?> getDataType(ColumnDefinition column) {
		// TODO: read datastructure
		return SQLDataType.VARCHAR(255).nullable(false);
	}

}

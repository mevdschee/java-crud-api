package com.tqdev.crudapi.service.definition;

import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;

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
		DataType<?> type = DefaultDataType.getDefaultDataType(column.getType());
		int length = column.getLength();
		int precision = column.getPrecision();
		int scale = column.getScale();
		boolean nullable = column.getNullable();
		if (length >= 0) {
			type.length(length);
		}
		if (precision >= 0) {
			type.precision(precision);
		}
		if (scale >= 0) {
			type.scale(scale);
		}
		type.nullable(nullable);
		return type;
	}

}

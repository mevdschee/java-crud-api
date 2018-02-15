package com.tqdev.crudapi.meta.definition;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jooq.Constraint;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.meta.reflection.ReflectedTable;

public class TableDefinition extends LinkedHashMap<String, ColumnDefinition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getPk() {
		for (String key : keySet()) {
			if (get(key).getPk() == true) {
				return key;
			}
		}
		return null;
	}

	public ArrayList<Field<?>> getFields(DSLContext dsl) {
		ArrayList<Field<?>> fields = new ArrayList<>();
		for (String columnName : keySet()) {
			ColumnDefinition column = get(columnName);
			fields.add(DSL.field(DSL.name(columnName), column.getDataType(dsl)));
		}
		return fields;
	}

	public ArrayList<Constraint> getPkConstraints(DSLContext dsl, String tableName) {
		ArrayList<Constraint> constraints = new ArrayList<>();
		String pk = getPk();
		if (pk != null) {
			constraints.add(DSL.constraint(DSL.name("pk_" + tableName)).primaryKey(DSL.field(DSL.name(pk))));
		}
		return constraints;
	}

	public ArrayList<Constraint> getFkConstraints(DSLContext dsl, String tableName, DatabaseDefinition definition)
			throws DatabaseDefinitionException {
		ArrayList<Constraint> constraints = new ArrayList<>();
		for (String columnName : keySet()) {
			ColumnDefinition column = get(columnName);
			String fk = column.getFk();
			if (fk != null) {
				String pk = definition.get(fk).getPk();
				if (pk == null) {
					throw new DatabaseDefinitionException(String.format(
							"Illegal 'fk' value for field '%s' of table '%s': Referenced table '%s' does not have a single field primary key",
							columnName, tableName, fk));
				}
				constraints.add(DSL.constraint(DSL.name("fk_" + tableName + "_" + columnName))
						.foreignKey(DSL.field(DSL.name(columnName)))
						.references(DSL.table(DSL.name(fk)), DSL.field(DSL.name(pk))));
			}
		}
		return constraints;
	}

	public static TableDefinition fromValue(ReflectedTable table) {
		TableDefinition definition = new TableDefinition();
		for (Field<?> field : table.fields()) {
			definition.put(field.getName(), ColumnDefinition.fromValue(field));
			definition.get(field.getName()).setFk(table.getFk(field.getName()));
		}
		Field<?> pk = table.getPk();
		if (pk != null) {
			definition.get(pk.getName()).setPk(true);
		}
		return definition;
	}

}

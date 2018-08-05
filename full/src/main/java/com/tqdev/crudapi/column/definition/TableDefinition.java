package com.tqdev.crudapi.column.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tqdev.crudapi.column.reflection.ReflectedTable;

import org.jooq.Constraint;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class TableDefinition {

	private String name = null;
	private LinkedHashMap<String, ColumnDefinition> columns = new LinkedHashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<ColumnDefinition> getColumns() {
		return columns.values();
	}

	public void setColumns(Collection<ColumnDefinition> columns) {
		this.columns = new LinkedHashMap<>();
		for (ColumnDefinition column : columns) {
			this.columns.put(column.getName(), column);
		}
	}

	@JsonIgnore
	public ColumnDefinition getPk() {
		for (String key : columns.keySet()) {
			ColumnDefinition column = columns.get(key);
			if (column.getPk() == true) {
				return column;
			}
		}
		return null;
	}

	public ArrayList<Field<?>> getFields(DSLContext dsl) {
		ArrayList<Field<?>> fields = new ArrayList<>();
		for (String columnName : columns.keySet()) {
			ColumnDefinition column = columns.get(columnName);
			fields.add(DSL.field(DSL.name(columnName), column.getDataType(dsl)));
		}
		return fields;
	}

	public ArrayList<Constraint> getPkConstraints(DSLContext dsl, String tableName) {
		ArrayList<Constraint> constraints = new ArrayList<>();
		ColumnDefinition pk = getPk();
		if (pk != null) {
			constraints.add(DSL.constraint(DSL.name("pk_" + tableName)).primaryKey(DSL.field(DSL.name(pk.getName()))));
		}
		return constraints;
	}

	public ArrayList<Constraint> getFkConstraints(DSLContext dsl, String tableName, DatabaseDefinition definition)
			throws DatabaseDefinitionException {
		ArrayList<Constraint> constraints = new ArrayList<>();
		for (String columnName : columns.keySet()) {
			ColumnDefinition column = columns.get(columnName);
			String fk = column.getFk();
			if (fk != null) {
				String pk = definition.get(fk).getPk().getName();
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

	public TableDefinition() {
		// nothing
	}

	public TableDefinition(ReflectedTable table) {
		setName(table.getName());
		for (Field<?> field : table.fields()) {
			String name = field.getName();
			ColumnDefinition column = new ColumnDefinition(field);
			column.setName(name);
			column.setFk(table.getFk(name));
			columns.put(name, column);
		}
		Field<?> pk = table.getPk();
		if (pk != null) {
			columns.get(pk.getName()).setPk(true);
		}
	}

}

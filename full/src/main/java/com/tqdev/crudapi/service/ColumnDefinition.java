package com.tqdev.crudapi.service;

import org.jooq.Field;

public class ColumnDefinition {
	private boolean pk;
	private String type;
	private String length;
	private boolean nullable;
	private String fk;

	public Boolean getPk() {
		return pk;
	}

	public void setPk(Boolean pk) {
		this.pk = pk;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public String getFk() {
		return fk;
	}

	public void setFk(String fk) {
		this.fk = fk;
	}

	public static ColumnDefinition fromValue(Field<?> field) {
		ColumnDefinition definition = new ColumnDefinition();
		definition.setPk(field.getDataType().identity());
		definition.setType(field.getDataType().getSQLDataType().getTypeName());
		definition.setLength(String.valueOf(field.getDataType().length()));
		definition.setNullable(field.getDataType().nullable());
		return definition;
	}
}

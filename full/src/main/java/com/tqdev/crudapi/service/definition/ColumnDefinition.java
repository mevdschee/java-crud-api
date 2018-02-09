package com.tqdev.crudapi.service.definition;

import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDataType;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ColumnDefinition {
	private boolean pk = false;
	private String type;
	private int length = -1;
	private int precision = -1;
	private int scale = -1;
	private boolean nullable = false;
	private String fk = null;

	public boolean getPk() {
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

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean getNullable() {
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

	// ugly hack
	@SuppressWarnings("unchecked")
	private DataType<?> getTypeByName(SQLDialect dialect, String type) {
		Map<String, DataType<?>>[] typesByName = null;
		java.lang.reflect.Field[] fields = DefaultDataType.class.getDeclaredFields();
		for (java.lang.reflect.Field field : fields) {
			if (field.getName().equals("TYPES_BY_NAME")) {
				field.setAccessible(true);
				try {
					typesByName = (Map<String, DataType<?>>[]) field.get(typesByName);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// should not happen
					throw new RuntimeException(
							"The command 'field.setAccessible(true);' on DefaultDataType's static 'TYPES_BY_NAME' failed");
				}
			}
		}
		return typesByName[dialect.ordinal()].get(DefaultDataType.normalise(type));
	}

	public DataType<?> getDataType(DSLContext dsl) {
		DataType<?> result = getTypeByName(dsl.dialect(), type);
		if (result == null) {
			result = DefaultDataType.getDefaultDataType(type);
		}
		result = result.identity(pk);
		if (length >= 0) {
			result = result.length(length);
		}
		if (precision >= 0) {
			result = result.precision(precision);
		}
		if (scale >= 0) {
			result = result.scale(scale);
		}
		result = result.nullable(nullable);
		return result;
	}

	public static ColumnDefinition fromValue(Field<?> field) {
		ColumnDefinition definition = new ColumnDefinition();
		definition.setPk(field.getDataType().identity());
		String typeName = field.getDataType().getTypeName();
		DataType<?> defaultType = DefaultDataType.getDefaultDataType(typeName);
		definition.setType(defaultType.getTypeName());
		if (field.getDataType().hasLength()) {
			definition.setLength(field.getDataType().length());
		}
		if (field.getDataType().hasPrecision()) {
			definition.setPrecision(field.getDataType().precision());
		}
		if (field.getDataType().hasScale()) {
			definition.setScale(field.getDataType().scale());
		}
		definition.setNullable(field.getDataType().nullable());
		return definition;
	}

}

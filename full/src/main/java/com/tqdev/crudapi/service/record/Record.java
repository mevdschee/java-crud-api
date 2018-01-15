package com.tqdev.crudapi.service.record;

import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tomcat.util.codec.binary.Base64;

import com.tqdev.crudapi.service.definition.ColumnDefinition;

public class Record extends LinkedHashMap<String, Object> {

	public static Record valueOf(Object object) {
		if (object instanceof Map<?, ?>) {
			return Record.valueOf((Map<?, ?>) object);
		}
		return null;
	}

	public static Record valueOf(Map<?, ?> map) {
		if (map != null) {
			Record result = new Record();
			for (Object key : map.keySet()) {
				result.put(key.toString(), map.get(key));
			}
			return result;
		}
		return null;
	}

	public Record copyColumns(Set<String> columns) {
		if (columns != null) {
			Record result = new Record();
			for (String key : columns) {
				result.put(key, get(key));
			}
			return result;
		}
		return null;
	}

	public static Record valueOf(org.jooq.Record record) {
		if (record != null) {
			return Record.valueOf(record.intoMap());
		}
		return null;
	}

	// see: https://www.tutorialspoint.com/jdbc/jdbc-data-types.htm
	private Object convertType(ColumnDefinition column, Object value) {
		if (value == null && column.getNullable() == true) {
			return null;
		}
		switch (column.getType().toUpperCase()) {
		case "VARCHAR":
		case "CHAR":
		case "LONGVARCHAR":
		case "STRING": /* non-JDBC type, for compatibility */
		case "TINYTEXT": /* non-JDBC type, for compatibility */
		case "TEXT": /* non-JDBC type, for compatibility */
		case "MEDIUMTEXT": /* non-JDBC type, for compatibility */
		case "LONGTEXT": /* non-JDBC type, for compatibility */
			if (value == null) {
				return new String("");
			} else {
				return String.valueOf(value);
			}
		case "VARBINARY":
		case "BIN": /* non-JDBC type, for compatibility */
		case "BINARY":
		case "CLOB":
		case "TINYBLOB": /* non-JDBC type, for compatibility */
		case "BLOB":
		case "MEDIUMBLOB": /* non-JDBC type, for compatibility */
		case "LONGBLOB": /* non-JDBC type, for compatibility */
			if (value == null) {
				return new String("");
			} else {
				return Base64.encodeBase64String(Base64.decodeBase64(String.valueOf(value)));
			}
		case "BIT":
		case "BOOL": /* non-JDBC type, for compatibility */
		case "BOOLEAN": /* non-JDBC type, for compatibility */
			if (value == null) {
				return new Boolean(false);
			} else if (value instanceof Boolean) {
				return value;
			} else if (value instanceof Number) {
				return (Integer) value > 0;
			} else if (value instanceof String) {
				char c = value.toString().toLowerCase().charAt(0);
				return c == '1' || c == 't';
			}
		case "NUMERIC":
		case "DECIMAL": /* non-JDBC type, for compatibility */
			if (value == null) {
				return new String("0");
			} else {
				java.math.BigDecimal n;
				if (column.getPrecision() < 0) {
					n = new java.math.BigDecimal(value.toString());
				} else {
					n = new java.math.BigDecimal(value.toString(),
							new MathContext(column.getPrecision(), RoundingMode.HALF_UP));
				}
				if (column.getPrecision() >= 0) {
					n = n.setScale(column.getScale(), RoundingMode.HALF_UP);
				}
				return n;
			}
		case "BYTE": /* non-JDBC type, for compatibility */
		case "TINYINT":
		case "SMALLINT":
		case "INTEGER":
		case "INT": /* non-JDBC type, for compatibility */
		case "BIGINT":
		case "LONG": /* non-JDBC type, for compatibility */
			if (value == null) {
				return new Long(0);
			} else {
				return Long.parseLong(value.toString());
			}
		case "REAL":
		case "FLOAT":
		case "DOUBLE":
		case "NUMBER": /* non-JDBC type, for compatibility */
			if (value == null) {
				return new Double(0);
			} else {
				return Double.parseDouble(value.toString());
			}
		case "DATE":
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			if (value == null) {
				return dateFormat.format(new Date(0));
			}
			if (value instanceof Number) {
				return dateFormat.format(new Date((Long) value));
			} else {
				try {
					Date date = dateFormat.parse(value.toString());
					return dateFormat.format(date);
				} catch (ParseException e) {
					return dateFormat.format(new Date(0));
				}
			}
		case "TIME":
		case "TIMESTAMP":
		case "DATETIME": /* non-JDBC type, for compatibility */
			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			if (value == null) {
				return timeFormat.format(new Date(0));
			}
			if (value instanceof Number) {
				return timeFormat.format(new Date((Long) value));
			} else {
				try {
					Date date = timeFormat.parse(value.toString());
					return timeFormat.format(date);
				} catch (ParseException e) {
					return timeFormat.format(new Date(0));
				}
			}
		case "ARRAY":
		case "REF":
		case "STRUCT":
		case "XML": /* non-JDBC type, for compatibility */
		case "OBJECT": /* non-JDBC type, for compatibility */
		case "JSON": /* non-JDBC type, for compatibility */
		case "GEOMETRY": /* non-JDBC type, for compatibility */
		default:
			throw new UnsupportedOperationException("Type \"" + column.getType() + "\" is not supported.");
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void putTyped(String key, Object value, ColumnDefinition column) {
		put(key, convertType(column, value));
	}

}

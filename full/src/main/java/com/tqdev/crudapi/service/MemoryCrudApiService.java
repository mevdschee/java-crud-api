package com.tqdev.crudapi.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryCrudApiService implements CrudApiService {

	private ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, ConcurrentHashMap<String, Record>> database = new ConcurrentHashMap<>();

	private DatabaseDefinition definition = new DatabaseDefinition();

	private String filename;

	public MemoryCrudApiService(String filename) {
		this.filename = filename;
		updateDefinition();
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
		case "VARBINARY":
		case "BINARY":
		case "CLOB":
		case "BLOB":
		case "STRING": /* non-JDBC type, for compatibility */
		case "TEXT": /* non-JDBC type, for compatibility */
			if (value == null) {
				return new String("");
			} else {
				return String.valueOf(value);
			}
		case "BIT":
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
				return new java.math.BigDecimal(value.toString()).toPlainString();
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
		case "JSON": /* non-JDBC type, for compatibility */
		case "GEOMETRY": /* non-JDBC type, for compatibility */
		default:
			throw new UnsupportedOperationException("Type \"" + column.getType() + "\" is not supported.");
		}
	}

	private void sanitizeRecord(String table, String id, Record record) {
		for (String key : record.keySet()) {
			if (!definition.get(table).containsKey(key)) {
				record.remove(key);
			} else {
				ColumnDefinition column = definition.get(table).get(key);
				record.put(key, convertType(column, record.get(key)));
			}
		}
		for (String key : definition.get(table).keySet()) {
			ColumnDefinition column = definition.get(table).get(key);
			if (!record.containsKey(key)) {
				record.put(key, convertType(column, null));
			}
			if (definition.get(table).get(key).getPk() == true) {
				record.put(key, convertType(column, id));
			}
		}
	}

	@Override
	public String create(String table, Record record) {
		if (database.containsKey(table)) {
			String id = String.valueOf(counters.get(table).incrementAndGet());
			sanitizeRecord(table, id, record);
			database.get(table).put(id, record);
			return id;
		}
		return null;
	}

	@Override
	public Record read(String table, String id) {
		if (database.containsKey(table)) {
			if (database.get(table).containsKey(id)) {
				return Record.valueOf(database.get(table).get(id));
			}
		}
		return null;
	}

	@Override
	public Integer update(String table, String id, Record record) {
		if (database.containsKey(table)) {
			sanitizeRecord(table, id, record);
			database.get(table).put(id, record);
			return 1;
		}
		return 0;
	}

	@Override
	public Integer delete(String table, String id) {
		if (database.containsKey(table) && database.get(table).containsKey(id)) {
			database.get(table).remove(id);
			return 1;
		}
		return 0;
	}

	@Override
	public ListResponse list(String table) {
		if (database.containsKey(table)) {
			return new ListResponse(database.get(table).values().toArray(new Record[] {}));
		}
		return null;
	}

	@Override
	public boolean updateDefinition() {
		DatabaseDefinition definition = DatabaseDefinition.fromValue(filename);
		if (definition != null) {
			applyDefinition(definition);
			return true;
		}
		return false;
	}

	private void applyDefinition(DatabaseDefinition definition) {
		for (String table : definition.keySet()) {
			if (!database.containsKey(table)) {
				ConcurrentHashMap<String, Record> records = new ConcurrentHashMap<>();
				counters.put(table, new AtomicLong());
				database.put(table, records);
			}
		}
		this.definition = definition;
		for (String table : database.keySet()) {
			if (!definition.containsKey(table)) {
				database.remove(table);
				counters.remove(table);
			}
		}
	}

}

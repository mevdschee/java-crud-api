package com.tqdev.crudapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ResultQuery;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.core.record.Record;
import com.tqdev.crudapi.meta.reflection.DatabaseReflection;
import com.tqdev.crudapi.meta.reflection.ReflectedTable;

public interface JooqIncluder {

	default public void addIncludes(String tableName, ArrayList<Record> records, DatabaseReflection tables,
			Params params, DSLContext dsl) {
		if (params.containsKey("include")) {
			for (String includedTableNames : params.get("include")) {
				ReflectedTable t1 = tables.get(tableName);
				ArrayList<ReflectedTable[]> includes = new ArrayList<>();
				for (String includedTableName : includedTableNames.split(",")) {
					ReflectedTable t2 = tables.get(includedTableName);
					if (t2 != null) {
						includes.add(new ReflectedTable[] { t1, t2 });
					}
					t1 = t2;
				}
				addIncludesForTables(includes, records, dsl);
			}
		}
	}

	default public void addIncludesForTables(ArrayList<ReflectedTable[]> includes, ArrayList<Record> records,
			DSLContext dsl) {
		if (includes.isEmpty()) {
			return;
		}

		ReflectedTable[] include = includes.remove(0);
		ReflectedTable t1 = include[0];
		ReflectedTable t2 = include[1];

		boolean belongsTo = !t1.getFksTo(t2.getName()).isEmpty();
		boolean hasMany = !t2.getFksTo(t1.getName()).isEmpty();

		ArrayList<Record> newRecords = new ArrayList<>();
		HashMap<Object, Object> fkValues = null;
		HashMap<Object, ArrayList<Object>> pkValues = null;

		if (belongsTo) {
			fkValues = getFkEmptyValues(t1, t2, records);
			addFkRecords(t2, fkValues, dsl, newRecords);
		}
		if (hasMany) {
			pkValues = getPkEmptyValues(t1, records);
			addPkRecords(t1, t2, pkValues, dsl, newRecords);
		}

		addIncludesForTables(includes, newRecords, dsl);

		if (fkValues != null) {
			fillFkValues(t2, newRecords, fkValues);
			setFkValues(t1, t2, records, fkValues);
		}
		if (pkValues != null) {
			fillPkValues(t1, t2, newRecords, pkValues);
			setPkValues(t1, t2, records, pkValues);
		}
	}

	default HashMap<Object, Object> getFkEmptyValues(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records) {
		HashMap<Object, Object> fkValues = new HashMap<>();
		List<Field<Object>> fks = t1.getFksTo(t2.getName());
		for (Field<Object> fk : fks) {
			for (Record record : records) {
				Object fkValue = record.get(fk.getName());
				if (fkValue == null) {
					continue;
				}
				fkValues.put(fkValue, null);
			}
		}
		return fkValues;
	}

	default void addFkRecords(ReflectedTable t2, HashMap<Object, Object> fkValues, DSLContext dsl,
			ArrayList<Record> records) {
		Field<Object> pk = t2.getPk();
		ResultQuery<org.jooq.Record> query = dsl.select(t2.fields()).from(t2).where(pk.in(fkValues.keySet()));
		for (org.jooq.Record record : query.fetch()) {
			records.add(Record.valueOf(record.intoMap()));
		}
	}

	default void fillFkValues(ReflectedTable t2, ArrayList<Record> fkRecords, HashMap<Object, Object> fkValues) {
		Field<Object> pk = t2.getPk();
		for (Record fkRecord : fkRecords) {
			Object pkValue = fkRecord.get(pk.getName());
			fkValues.put(pkValue, fkRecord);
		}
	}

	default void setFkValues(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records,
			HashMap<Object, Object> fkValues) {
		List<Field<Object>> fks = t1.getFksTo(t2.getName());
		for (Field<Object> fk : fks) {
			for (Record record : records) {
				Object key = record.get(fk.getName());
				if (key == null) {
					continue;
				}
				record.put(fk.getName(), fkValues.get(key));
			}
		}
	}

	default HashMap<Object, ArrayList<Object>> getPkEmptyValues(ReflectedTable t1, ArrayList<Record> records) {
		HashMap<Object, ArrayList<Object>> pkValues = new HashMap<>();
		for (Record record : records) {
			Object key = record.get(t1.getPk().getName());
			pkValues.put(key, new ArrayList<>());
		}
		return pkValues;
	}

	default void addPkRecords(ReflectedTable t1, ReflectedTable t2, HashMap<Object, ArrayList<Object>> pkValues,
			DSLContext dsl, ArrayList<Record> records) {
		List<Field<Object>> fks = t2.getFksTo(t1.getName());
		Condition condition = DSL.falseCondition();
		for (Field<Object> fk : fks) {
			if (condition == null) {
				condition = fk.in(pkValues.keySet());
			} else {
				condition = condition.or(fk.in(pkValues.keySet()));
			}
		}
		ResultQuery<org.jooq.Record> query = dsl.select(t2.fields()).from(t2).where(condition);
		for (org.jooq.Record record : query.fetch()) {
			records.add(Record.valueOf(record.intoMap()));
		}
	}

	default void fillPkValues(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> pkRecords,
			HashMap<Object, ArrayList<Object>> pkValues) {
		List<Field<Object>> fks = t2.getFksTo(t1.getName());
		for (Field<Object> fk : fks) {
			for (Record pkRecord : pkRecords) {
				Object key = pkRecord.get(fk.getName());
				ArrayList<Object> records = pkValues.get(key);
				if (records != null) {
					records.add(pkRecord);
				}
			}
		}
	}

	default void setPkValues(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records,
			HashMap<Object, ArrayList<Object>> pkValues) {
		for (Record record : records) {
			Object key = record.get(t1.getPk().getName());
			record.put(t2.getName(), pkValues.get(key));
		}
	}

}
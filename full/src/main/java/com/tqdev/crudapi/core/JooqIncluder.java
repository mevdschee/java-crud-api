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
		ReflectedTable t1 = tables.get(tableName);
		if (t1 == null) {
			return;
		}
		if (params.containsKey("include")) {
			for (String includedTableName : params.get("include")) {
				ReflectedTable t2 = tables.get(includedTableName);
				if (t2 == null) {
					continue;
				}
				if (!t1.getFksTo(t2.getName()).isEmpty()) {
					addBelongsTo(t1, t2, records, dsl);
				}
				if (!t2.getFksTo(t1.getName()).isEmpty()) {
					addHasMany(t1, t2, records, dsl);
				}
			}
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

	default ArrayList<Record> getFkRecords(ReflectedTable t2, HashMap<Object, Object> fkValues, DSLContext dsl) {
		ArrayList<Record> fkRecords = new ArrayList<>();
		Field<Object> pk = t2.getPk();
		ResultQuery<org.jooq.Record> query = dsl.select(t2.fields()).from(t2).where(pk.in(fkValues.keySet()));
		for (org.jooq.Record record : query.fetch()) {
			fkRecords.add(Record.valueOf(record.intoMap()));
		}
		return fkRecords;
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

	default public void addBelongsTo(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records, DSLContext dsl) {
		HashMap<Object, Object> fkValues = getFkEmptyValues(t1, t2, records);
		ArrayList<Record> fkRecords = getFkRecords(t2, fkValues, dsl);
		// recurse?
		fillFkValues(t2, fkRecords, fkValues);
		setFkValues(t1, t2, records, fkValues);
	}

	default HashMap<Object, ArrayList<Object>> getPkEmptyValues(ReflectedTable t1, ArrayList<Record> records) {
		HashMap<Object, ArrayList<Object>> pkValues = new HashMap<>();
		for (Record record : records) {
			Object key = record.get(t1.getPk().getName());
			pkValues.put(key, new ArrayList<>());
		}
		return pkValues;
	}

	default ArrayList<Record> getPkRecords(ReflectedTable t1, ReflectedTable t2,
			HashMap<Object, ArrayList<Object>> pkValues, DSLContext dsl) {
		ArrayList<Record> pkRecords = new ArrayList<>();
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
			pkRecords.add(Record.valueOf(record.intoMap()));
		}
		return pkRecords;
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

	default public void addHasMany(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records, DSLContext dsl) {
		HashMap<Object, ArrayList<Object>> pkValues = getPkEmptyValues(t1, records);
		ArrayList<Record> pkRecords = getPkRecords(t1, t2, pkValues, dsl);
		// recurse?
		fillPkValues(t1, t2, pkRecords, pkValues);
		setPkValues(t1, t2, records, pkValues);
	}
}
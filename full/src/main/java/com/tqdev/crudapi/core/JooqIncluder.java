package com.tqdev.crudapi.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ResultQuery;

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
				addBelongsTo(t1, t2, records, dsl);
			}
		}
	}

	default HashMap<Object, Object> getFkValues(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records) {
		HashMap<Object, Object> fkValues = new HashMap<>();
		for (String fieldName : t1.fieldNames()) {
			String fkTableName = t1.getFk(fieldName);
			if (fkTableName == null || !fkTableName.equals(t2.getName())) {
				continue;
			}
			for (Record record : records) {
				Object fkValue = record.get(fieldName);
				if (fkValue == null) {
					continue;
				}
				fkValues.put(fkValue, null);
			}
		}
		return fkValues;
	}

	default void fillFkValues(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records,
			HashMap<Object, Object> fkValues, DSLContext dsl) {
		Field<Object> pk = t2.getPk();
		ResultQuery<org.jooq.Record> query = dsl.select(t2.fields()).from(t2).where(pk.in(fkValues.keySet()));
		for (org.jooq.Record record : query.fetch()) {
			Record r = Record.valueOf(record.intoMap());
			Object pkValue = r.get(pk.getName());
			fkValues.put(pkValue, r);
		}
	}

	default void setFkValues(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records,
			HashMap<Object, Object> fkValues) {
		for (String fieldName : t1.fieldNames()) {
			String fkTableName = t1.getFk(fieldName);
			if (fkTableName == null || !fkTableName.equals(t2.getName())) {
				continue;
			}
			for (Record record : records) {
				Object fkValue = record.get(fieldName);
				if (fkValue == null) {
					continue;
				}
				record.put(fieldName, fkValues.get(fkValue));
			}
		}
	}

	default public void addBelongsTo(ReflectedTable t1, ReflectedTable t2, ArrayList<Record> records, DSLContext dsl) {
		HashMap<Object, Object> fkValues = getFkValues(t1, t2, records);
		fillFkValues(t1, t2, records, fkValues, dsl);
		setFkValues(t1, t2, records, fkValues);
	}
}
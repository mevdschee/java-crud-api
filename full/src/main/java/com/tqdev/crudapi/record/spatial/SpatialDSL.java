package com.tqdev.crudapi.record.spatial;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDataType;

public class SpatialDSL {

	public static Field<?> asText(Field<?> field) {
		return new AsText(field);
	}

	public static Field<?> geomFromText(Field<?> field) {
		return new GeomFromText(field);
	}

	public static Condition contains(Field<?> field1, Field<?> field2) {
		return new Contains(field1, field2);
	}

	public static Condition crosses(Field<?> field1, Field<?> field2) {
		return new Crosses(field1, field2);
	}

	public static Condition disjoint(Field<?> field1, Field<?> field2) {
		return new Disjoint(field1, field2);
	}

	public static Condition equals(Field<?> field1, Field<?> field2) {
		return new Equals(field1, field2);
	}

	public static Condition intersects(Field<?> field1, Field<?> field2) {
		return new Intersects(field1, field2);
	}

	public static Condition overlaps(Field<?> field1, Field<?> field2) {
		return new Overlaps(field1, field2);
	}

	public static Condition touches(Field<?> field1, Field<?> field2) {
		return new Touches(field1, field2);
	}

	public static Condition within(Field<?> field1, Field<?> field2) {
		return new Within(field1, field2);
	}

	public static Condition isClosed(Field<?> field) {
		return new IsClosed(field);
	}

	public static Condition isSimple(Field<?> field) {
		return new IsSimple(field);
	}

	public static Condition isValid(Field<?> field) {
		return new IsValid(field);
	}

	public static void registerDataTypes(DSLContext dsl) {
		SQLDialect dialect = dsl.dialect();
		switch (dialect.family().toString()) {
		case "MYSQL":
		case "POSTGRES":
		case "SQLSERVER":
			DefaultDataType.getDefaultDataType(SQLDialect.DEFAULT, "geometry");
			break;
		}
	}

}

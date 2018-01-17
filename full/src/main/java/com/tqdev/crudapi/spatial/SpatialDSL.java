package com.tqdev.crudapi.spatial;

import org.jooq.Field;
import org.jooq.impl.DSL;

public class SpatialDSL {
	public static Field<String> asText(Field<?> field) {
		return new AsText(field);
	}

	public static Field<byte[]> geomFromText(Field<?> field) {
		return new GeomFromText(field);
	}

	public static Field<Boolean> within(Field<?> field1, Field<?> field2) {
		return DSL.field("ST_Within({0}, {1})", Boolean.class, field1, field2);
	}

}

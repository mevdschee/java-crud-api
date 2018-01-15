package com.tqdev.crudapi.spatial;

import org.jooq.Field;
import org.jooq.impl.DSL;

public class SpatialDSL {
	public static Field<String> toChar(Field<?> field, String format) {
		return new ToChar(field, DSL.inline(format));
	}
}

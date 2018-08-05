package com.tqdev.crudapi.record.spatial;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.impl.CustomCondition;
import org.jooq.impl.DSL;

public class Contains extends CustomCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final Field<?> field1;
	final Field<?> field2;

	Contains(Field<?> field1, Field<?> field2) {
		super();
		this.field1 = field1;
		this.field2 = field2;
	}

	@Override
	public void accept(Context<?> context) {
		context.visit(delegate(context.configuration()));
	}

	private QueryPart delegate(Configuration configuration) {
		switch (configuration.dialect().family().toString()) {
		case "MYSQL":
		case "POSTGRES":
			return DSL.field("ST_Contains({0}, {1})", Boolean.class, field1, field2);
		case "SQLSERVER":
			return DSL.field("{0}.STContains({1})", Boolean.class, field1, field2);
		default:
			throw new UnsupportedOperationException("Dialect not supported");
		}
	}
}

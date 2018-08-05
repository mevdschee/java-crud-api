package com.tqdev.crudapi.record.spatial;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.impl.CustomField;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

class AsText extends CustomField<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final Field<?> field;

	AsText(Field<?> field) {
		super("st_astext", SQLDataType.VARCHAR);
		this.field = field;
	}

	@Override
	public void accept(Context<?> context) {
		context.visit(delegate(context.configuration()));
	}

	private QueryPart delegate(Configuration configuration) {
		switch (configuration.dialect().family().toString()) {
		case "MYSQL":
		case "POSTGRES":
			return DSL.field("ST_AsText({0})", String.class, field);
		case "SQLSERVER":
			return DSL.field("{0}.STAsText(0)", String.class, field);
		default:
			throw new UnsupportedOperationException("Dialect not supported");
		}
	}

}
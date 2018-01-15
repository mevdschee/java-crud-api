package com.tqdev.crudapi.spatial;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.impl.CustomField;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

//Create a CustomField implementation taking two arguments in its constructor
class ToChar extends CustomField<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final Field<?> arg0;
	final Field<?> arg1;

	ToChar(Field<?> arg0, Field<?> arg1) {
		super("to_char", SQLDataType.VARCHAR);

		this.arg0 = arg0;
		this.arg1 = arg1;
	}

	@Override
	public void accept(Context<?> context) {
		context.visit(delegate(context.configuration()));
	}

	private QueryPart delegate(Configuration configuration) {
		switch (configuration.dialect().family().getName()) {
		case "ORACLE":
			return DSL.field("TO_CHAR({0}, {1})", String.class, arg0, arg1);

		case "SQLSERVER":
			return DSL.field("CONVERT(VARCHAR(8), {0}, {1})", String.class, arg0, arg1);

		default:
			throw new UnsupportedOperationException("Dialect not supported");
		}
	}

}
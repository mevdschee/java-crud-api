package com.tqdev.crudapi.spatial;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;

import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.impl.DSL;

import com.google.gson.*;

// We're binding <T> = Object (unknown JDBC type), and <U> = JsonElement (user type)
public class PostgresJSONGsonBinding implements Binding<Object, JsonElement> {

	// The converter does all the work
	@Override
	public Converter<Object, JsonElement> converter() {
		return new Converter<Object, JsonElement>() {
			@Override
			public JsonElement from(Object t) {
				return t == null ? JsonNull.INSTANCE : new Gson().fromJson("" + t, JsonElement.class);
			}

			@Override
			public Object to(JsonElement u) {
				return u == null || u == JsonNull.INSTANCE ? null : new Gson().toJson(u);
			}

			@Override
			public Class<Object> fromType() {
				return Object.class;
			}

			@Override
			public Class<JsonElement> toType() {
				return JsonElement.class;
			}
		};
	}

	// Rending a bind variable for the binding context's value and casting it to
	// the json type
	@Override
	public void sql(BindingSQLContext<JsonElement> ctx) throws SQLException {
		// Depending on how you generate your SQL, you may need to explicitly
		// distinguish
		// between jOOQ generating bind variables or inlined literals. If so,
		// use this check:
		// ctx.render().paramType() == INLINED
		ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::json");
	}

	// Registering VARCHAR types for JDBC CallableStatement OUT parameters
	@Override
	public void register(BindingRegisterContext<JsonElement> ctx) throws SQLException {
		ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
	}

	// Converting the JsonElement to a String value and setting that on a JDBC
	// PreparedStatement
	@Override
	public void set(BindingSetStatementContext<JsonElement> ctx) throws SQLException {
		ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
	}

	// Getting a String value from a JDBC ResultSet and converting that to a
	// JsonElement
	@Override
	public void get(BindingGetResultSetContext<JsonElement> ctx) throws SQLException {
		ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
	}

	// Getting a String value from a JDBC CallableStatement and converting that
	// to a JsonElement
	@Override
	public void get(BindingGetStatementContext<JsonElement> ctx) throws SQLException {
		ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
	}

	// Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
	@Override
	public void set(BindingSetSQLOutputContext<JsonElement> ctx) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	// Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
	@Override
	public void get(BindingGetSQLInputContext<JsonElement> ctx) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
}
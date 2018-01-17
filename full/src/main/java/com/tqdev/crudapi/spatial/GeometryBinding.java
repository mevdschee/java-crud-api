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

// We're binding <T> = Object (unknown JDBC type), and <U> = Geometry (user type)
public class GeometryBinding implements Binding<Object, Geometry> {

	// The converter does all the work
	@Override
	public Converter<Object, Geometry> converter() {
		return new Converter<Object, Geometry>() {
			@Override
			public Geometry from(Object t) {
				return t == null ? null : new Geometry((byte[]) t);
			}

			@Override
			public Object to(Geometry u) {
				return u == null ? null : u.getBytes();
			}

			@Override
			public Class<Object> fromType() {
				return Object.class;
			}

			@Override
			public Class<Geometry> toType() {
				return Geometry.class;
			}
		};
	}

	// Rending a bind variable for the binding context's value and casting it to
	// the json type
	@Override
	public void sql(BindingSQLContext<Geometry> ctx) throws SQLException {
		// Depending on how you generate your SQL, you may need to explicitly
		// distinguish
		// between jOOQ generating bind variables or inlined literals. If so,
		// use this check:
		// ctx.render().paramType() == INLINED
		ctx.render().visit(DSL.val(ctx.convert(converter()).value()));
	}

	// Registering VARCHAR types for JDBC CallableStatement OUT parameters
	@Override
	public void register(BindingRegisterContext<Geometry> ctx) throws SQLException {
		ctx.statement().registerOutParameter(ctx.index(), Types.VARBINARY);
	}

	// Converting the Geometry to a String value and setting that on a JDBC
	// PreparedStatement
	@Override
	public void set(BindingSetStatementContext<Geometry> ctx) throws SQLException {
		ctx.statement().setBytes(ctx.index(), (byte[]) ctx.convert(converter()).value());
	}

	// Getting a String value from a JDBC ResultSet and converting that to a
	// Geometry
	@Override
	public void get(BindingGetResultSetContext<Geometry> ctx) throws SQLException {
		ctx.convert(converter()).value(ctx.resultSet().getBytes(ctx.index()));
	}

	// Getting a String value from a JDBC CallableStatement and converting that
	// to a Geometry
	@Override
	public void get(BindingGetStatementContext<Geometry> ctx) throws SQLException {
		ctx.convert(converter()).value(ctx.statement().getBytes(ctx.index()));
	}

	// Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
	@Override
	public void set(BindingSetSQLOutputContext<Geometry> ctx) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	// Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
	@Override
	public void get(BindingGetSQLInputContext<Geometry> ctx) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
}
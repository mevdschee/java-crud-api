package com.tqdev.crudapi.spatial;

import org.jooq.Converter;

public class GeometryConverter implements Converter<Object, Geometry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
}

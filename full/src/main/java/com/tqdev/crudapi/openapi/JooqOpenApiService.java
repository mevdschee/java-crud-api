package com.tqdev.crudapi.openapi;

import com.tqdev.crudapi.column.ColumnService;
import com.tqdev.crudapi.column.reflection.DatabaseReflection;
import org.jooq.DSLContext;

import java.io.IOException;

public class JooqOpenApiService implements OpenApiService {

	private DSLContext dsl;
	private DatabaseReflection reflection;
	private OpenApiDefinition baseOpenApiDefinition;

	public JooqOpenApiService(DSLContext dsl, ColumnService columns) {
		this.dsl = dsl;
		reflection = columns.getDatabaseReflection();
		baseOpenApiDefinition = null;
	}

	@Override
	public OpenApiDefinition getOpenApiDefinition() {
		OpenApiBuilder builder = new OpenApiBuilder(reflection,baseOpenApiDefinition);
		return builder.build();
	}

	@Override
	public void initialize(String openApiFilename) throws IOException {
		baseOpenApiDefinition = OpenApiDefinition.fromFile(openApiFilename);
	}

}

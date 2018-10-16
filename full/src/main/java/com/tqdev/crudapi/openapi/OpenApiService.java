package com.tqdev.crudapi.openapi;

import java.io.IOException;

public interface OpenApiService {

	// meta

	OpenApiDefinition getOpenApiDefinition();

	// initialization

	void initialize(String openApiFilename) throws IOException;

}

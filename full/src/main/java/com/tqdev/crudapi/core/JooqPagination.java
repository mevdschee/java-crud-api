package com.tqdev.crudapi.core;

public interface JooqPagination {

	default public boolean hasPagination(Params params) {
		return params.containsKey("page");
	}

	default public int offset(Params params) {
		int offset = 0;
		int numberOfRows = numberOfRows(params);
		if (params.containsKey("page")) {
			for (String key : params.get("page")) {
				String[] parts = key.split(",", 2);
				offset = (Integer.valueOf(parts[0]) - 1) * numberOfRows;
			}
		}
		return offset;
	}

	default public int numberOfRows(Params params) {
		int numberOfRows = 20;
		if (params.containsKey("page")) {
			for (String key : params.get("page")) {
				String[] parts = key.split(",", 2);
				if (parts.length > 1) {
					numberOfRows = Integer.valueOf(parts[1]);
				}
			}
		}
		return numberOfRows;
	}

}
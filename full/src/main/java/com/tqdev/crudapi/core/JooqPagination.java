package com.tqdev.crudapi.core;

public interface JooqPagination {

	default public boolean hasPagination(Params params) {
		return params.containsKey("page");
	}

	default public int pageOffset(Params params) {
		int offset = 0;
		int pageSize = pageSize(params);
		if (params.containsKey("page")) {
			for (String key : params.get("page")) {
				String[] parts = key.split(",", 2);
				int page = Integer.valueOf(parts[0]) - 1;
				offset = page * pageSize;
			}
		}
		return offset;
	}

	default public int pageSize(Params params) {
		int pageSize = 20;
		if (params.containsKey("page")) {
			for (String key : params.get("page")) {
				String[] parts = key.split(",", 2);
				if (parts.length > 1) {
					pageSize = Integer.valueOf(parts[1]);
				}
			}
		}
		return pageSize;
	}

}
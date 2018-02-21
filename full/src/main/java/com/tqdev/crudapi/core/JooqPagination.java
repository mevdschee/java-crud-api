package com.tqdev.crudapi.core;

import java.util.ArrayList;

public interface JooqPagination {

	public static final int DEFAULT_PAGE_SIZE = 20;

	default public boolean hasPage(Params params) {
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
		int pageSize = DEFAULT_PAGE_SIZE;
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

	default public Object[] seekAfter(Params params) {
		ArrayList<Object> values = new ArrayList<>();
		int count = 0;
		if (params.containsKey("order")) {
			for (String key : params.get("order")) {
				String[] parts = key.split(",", 3);
				if (parts.length > 2) {
					values.add(parts[2]);
					count++;
				} else {
					values.add(null);
				}
			}
		}
		return count > 0 ? values.toArray() : null;
	}

	default public int resultSize(Params params) {
		int numberOfRows = -1;
		if (params.containsKey("size")) {
			for (String key : params.get("size")) {
				numberOfRows = Integer.valueOf(key);
			}
		}
		return numberOfRows;
	}

}
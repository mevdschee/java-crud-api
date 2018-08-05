package com.tqdev.crudapi.record;

public class PaginationInfo {

	public final int DEFAULT_PAGE_SIZE = 20;

	public boolean hasPage(Params params) {
		return params.containsKey("page");
	}

	public int getPageOffset(Params params) {
		int offset = 0;
		int pageSize = getPageSize(params);
		if (params.containsKey("page")) {
			for (String key : params.get("page")) {
				String[] parts = key.split(",", 2);
				int page = Integer.valueOf(parts[0]) - 1;
				offset = page * pageSize;
			}
		}
		return offset;
	}

	public int getPageSize(Params params) {
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

	public int getResultSize(Params params) {
		int numberOfRows = -1;
		if (params.containsKey("size")) {
			for (String key : params.get("size")) {
				numberOfRows = Integer.valueOf(key);
			}
		}
		return numberOfRows;
	}

}
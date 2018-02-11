package com.tqdev.crudapi.core.record;

public class ListResponse {

	private Record[] records;

	public ListResponse(Record[] records) {
		this.records = records;
	}

	public Record[] getRecords() {
		return records;
	}

}

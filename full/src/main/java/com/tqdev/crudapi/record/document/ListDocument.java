package com.tqdev.crudapi.record.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tqdev.crudapi.record.container.Record;

public class ListDocument {

	private Record[] records;

	@JsonInclude(Include.NON_DEFAULT)
	private int results;

	public ListDocument(Record[] records, int results) {
		this.records = records;
		this.results = results;
	}

	public Record[] getRecords() {
		return records;
	}

	public int getResults() {
		return results;
	}
}

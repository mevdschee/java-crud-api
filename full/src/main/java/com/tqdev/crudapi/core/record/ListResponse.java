package com.tqdev.crudapi.core.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ListResponse {

	@JacksonXmlElementWrapper(localName = "Records")
	@JacksonXmlProperty(localName = "Record")
	private Record[] records;

	@JsonInclude(Include.NON_DEFAULT)
	private int results;

	public ListResponse(Record[] records, int results) {
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

package com.tqdev.crudapi.core.record;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ListResponse {

	@JacksonXmlElementWrapper(localName = "Records")
	@JacksonXmlProperty(localName = "Record")
	private Record[] records;

	public ListResponse(Record[] records) {
		this.records = records;
	}

	public Record[] getRecords() {
		return records;
	}

}

package com.tqdev.crudapi.data.record;

import java.util.ArrayList;

import com.tqdev.crudapi.data.DataService;
import com.tqdev.crudapi.data.Params;

public class TableRecords {

	private String name = null;
	private ArrayList<Record> records = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Record> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<Record> records) {
		this.records = records;
	}

	public TableRecords() {
		// nothing
	}

	public TableRecords(String name, ArrayList<Record> records) {
		this.name = name;
		this.records = records;
	}

	public void create(DataService service) throws DatabaseRecordsException {
		for (Record record : records) {
			if (!service.exists(name)) {
				throw new DatabaseRecordsException(
						String.format("Cannot insert into table '%s': Table does not exist", name));
			}
			service.create(name, record, new Params());
		}
	}

	public void put(ArrayList<Record> records) {
		this.records = records;
	}
}

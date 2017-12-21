package com.tqdev.crudapi.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.ListResponse;
import com.tqdev.crudapi.service.Record;

@Service
public class CrudApiServiceImpl implements CrudApiService {

	private static ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

	private static ConcurrentHashMap<String, ConcurrentHashMap<String, Record>> database = new ConcurrentHashMap<>();

	@Override
	public String create(String table, Record record) {
		String id = String.valueOf(counters.get(table).incrementAndGet());
		if (database.containsKey(table)) {
			record.put("id", id);
			database.get(table).put(id, record);
			return id;
		}
		return null;
	}

	@Override
	public Record read(String table, String id) {
		if (database.containsKey(table)) {
			if (database.get(table).containsKey(id)) {
				return Record.valueOf(database.get(table).get(id));
			}
		}
		return null;
	}

	@Override
	public Integer update(String table, String id, Record record) {
		if (database.containsKey(table)) {
			record.put("id", id);
			database.get(table).put(id, record);
			return 1;
		}
		return 0;
	}

	@Override
	public Integer delete(String table, String id) {
		if (database.containsKey(table) && database.get(table).containsKey(id)) {
			database.get(table).remove(id);
			return 1;
		}
		return 0;
	}

	@Override
	public ListResponse list(String table) {
		if (database.containsKey(table)) {
			ListResponse result = new ListResponse();
			result.records = database.get(table).values().toArray(new Record[] {});
			return result;
		}
		return null;
	}

	@Override
	public boolean dropTable(String table) {
		if (database.containsKey(table)) {
			counters.remove(table);
			database.remove(table);
			return true;
		}
		return false;
	}

	@Override
	public boolean createTable(String table, String definition) {
		ConcurrentHashMap<String, Record> records = new ConcurrentHashMap<>();
		if (!database.containsKey(table)) {
			counters.put(table, new AtomicLong());
			database.put(table, records);
			return true;
		}
		return false;
	}

}

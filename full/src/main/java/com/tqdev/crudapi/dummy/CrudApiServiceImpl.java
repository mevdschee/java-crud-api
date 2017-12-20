package com.tqdev.crudapi.dummy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.tqdev.crudapi.model.User;
import com.tqdev.crudapi.service.CrudApiService;
import com.tqdev.crudapi.service.ListResponse;

@Service
public class CrudApiServiceImpl implements CrudApiService {

	private static final AtomicLong counter = new AtomicLong();

	private static ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> database;

	static {
		database = populateUsers();
	}

	private static ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> populateUsers() {
		ConcurrentHashMap<String, Object> table;
		String id;
		table = new ConcurrentHashMap<>();
		id = String.valueOf(counter.incrementAndGet());
		table.put(id, new User(id, "Sam", 20, 70000));
		id = String.valueOf(counter.incrementAndGet());
		table.put(id, new User(id, "Max", 60, 50000));
		id = String.valueOf(counter.incrementAndGet());
		table.put(id, new User(id, "Abby", 33, 40000));
		id = String.valueOf(counter.incrementAndGet());
		table.put(id, new User(id, "Jack", 60, 30000));

		ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> database = new ConcurrentHashMap<>();
		database.put("users", table);
		return database;
	}

	@Override
	public String create(String entity, Object record) {
		String id = String.valueOf(counter.incrementAndGet());
		if (database.containsKey(entity)) {
			database.get(entity).put(id, record);
			return id;
		}
		return null;
	}

	@Override
	public Object read(String entity, String id) {
		if (database.containsKey(entity)) {
			if (database.get(entity).containsKey(id)) {
				return database.get(entity).get(id);
			}
		}
		return null;
	}

	@Override
	public Integer update(String entity, String id, Object record) {
		if (database.containsKey(entity)) {
			database.get(entity).put(id, record);
			return 1;
		}
		return 0;
	}

	@Override
	public Integer delete(String entity, String id) {
		if (database.containsKey(entity) && database.get(entity).containsKey(id)) {
			database.get(entity).remove(id);
			return 1;
		}
		return 0;
	}

	@Override
	public ListResponse list(String entity) {
		if (database.containsKey(entity)) {
			ListResponse result = new ListResponse();
			result.records = database.get(entity).values().toArray(new Object[] {});
			return result;
		}
		return null;
	}

}

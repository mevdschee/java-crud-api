package com.tqdev.crudapi.api;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

public class TreeMap<T> {
	/**
	 * 
	 */
	private LinkedHashMap<T, TreeMap<T>> branches = new LinkedHashMap<>();

	public void put(LinkedList<T> path) {
		if (path.isEmpty()) {
			return;
		}
		T key = path.removeFirst();
		TreeMap<T> val = branches.get(key);
		if (val == null) {
			val = new TreeMap<>();
			branches.put(key, val);
		}
		val.put(path);
	}

	public Set<T> getKeys() {
		return branches.keySet();
	}

	public TreeMap<T> get(T t) {
		return branches.get(t);
	}
}

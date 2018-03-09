package com.tqdev.crudapi.core;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class TreeMap<T> extends LinkedHashMap<T, TreeMap<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void put(T[] path) {
		LinkedList<T> list = new LinkedList<>();
		for (T key : path) {
			list.add(key);
		}
		put(list);
	}

	public void put(LinkedList<T> path) {
		if (path.isEmpty()) {
			return;
		}
		T key = path.removeFirst();
		TreeMap<T> val = get(key);
		if (val == null) {
			val = new TreeMap<>();
			put(key, val);
		}
		val.put(path);
	}

}

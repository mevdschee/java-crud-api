package com.tqdev.crudapi.record;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

public class PathTree<P, T> {

	private ArrayList<T> values = new ArrayList<>();

	private LinkedHashMap<P, PathTree<P, T>> branches = new LinkedHashMap<>();

	public ArrayList<T> getValues() {
		return values;
	}

	public void put(LinkedList<P> path, T value) {
		if (path.isEmpty()) {
			values.add(value);
			return;
		}
		P key = path.removeFirst();
		PathTree<P, T> val = branches.get(key);
		if (val == null) {
			val = new PathTree<>();
			branches.put(key, val);
		}
		val.put(path, value);
	}

	public Set<P> getKeys() {
		return branches.keySet();
	}

	public PathTree<P, T> get(P p) {
		return branches.get(p);
	}

}

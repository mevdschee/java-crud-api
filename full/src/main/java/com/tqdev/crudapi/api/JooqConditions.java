package com.tqdev.crudapi.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.api.spatial.SpatialDSL;
import com.tqdev.crudapi.meta.reflection.ReflectedTable;

public class JooqConditions {

	private static class PathTree<P, T> extends LinkedHashMap<P, PathTree<P, T>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1426969425705646921L;

		private ArrayList<T> values = new ArrayList<>();

		public ArrayList<T> getValues() {
			return values;
		}

		public void put(LinkedList<P> path, T value) {
			if (path.isEmpty()) {
				values.add(value);
				return;
			}
			P key = path.removeFirst();
			PathTree<P, T> val = get(key);
			if (val == null) {
				val = new PathTree<>();
				put(key, val);
			}
			val.put(path, value);
		}

	}

	private static PathTree<Character, Condition> getConditionsAsPathTree(ReflectedTable table, Params params) {
		PathTree<Character, Condition> conditions = new PathTree<>();
		if (params.containsKey("filter")) {
			for (String value : params.get("filter")) {
				Condition condition = getConditionFromString(table, value);
				if (condition != null) {
					LinkedList<Character> path = new LinkedList<Character>();
					conditions.put(path, condition);
				}
			}
		}
		for (char n = '0'; n <= '9'; n++) {
			if (params.containsKey("filter" + n)) {
				for (String value : params.get("filter" + n)) {
					Condition condition = getConditionFromString(table, value);
					if (condition != null) {
						LinkedList<Character> path = new LinkedList<Character>();
						path.add(n);
						conditions.put(path, condition);
					}
				}
			}
			for (char l = 'a'; l <= 'f'; l++) {
				if (params.containsKey("filter" + n + l)) {
					for (String value : params.get("filter" + n + l)) {
						Condition condition = getConditionFromString(table, value);
						if (condition != null) {
							LinkedList<Character> path = new LinkedList<Character>();
							path.add(n);
							path.add(l);
							conditions.put(path, condition);
						}
					}
				}
			}
		}
		return conditions;
	}

	private static Condition combinePathTreeOfConditions(PathTree<Character, Condition> tree) {
		ArrayList<Condition> conditions = tree.getValues();
		Condition and = null;
		for (Condition condition : conditions) {
			if (and == null) {
				and = condition;
			} else {
				and = and.and(condition);
			}
		}
		if (tree.keySet().size() == 0) {
			return and;
		}
		Condition or = null;
		for (Character p : tree.keySet()) {
			Condition condition = combinePathTreeOfConditions(tree.get(p));
			if (or == null) {
				or = condition;
			} else {
				or = or.or(condition);
			}
		}
		if (and == null) {
			and = or;
		} else {
			and = and.and(or);
		}
		return and;
	}

	public static ArrayList<Condition> conditions(ReflectedTable table, Params params) {
		ArrayList<Condition> conditions = new ArrayList<>();
		Condition condition = combinePathTreeOfConditions(getConditionsAsPathTree(table, params));
		if (condition != null) {
			conditions.add(condition);
		}
		return conditions;
	}

	private static Condition getConditionFromString(ReflectedTable table, String value) {
		Condition condition = null;
		String[] parts2;
		String[] parts = value.split(",", 3);
		if (parts.length < 2) {
			return null;
		}
		String command = parts[1];
		Boolean negate = false;
		Boolean spatial = false;
		if (command.length() > 2) {
			if (command.charAt(0) == 'n') {
				negate = true;
				command = command.substring(1);
			}
			if (command.charAt(0) == 's') {
				spatial = true;
				command = command.substring(1);
			}
		}
		Field<Object> field = table.get(parts[0]);
		if (parts.length == 3
				|| (parts.length == 2 && (command.equals("ic") || command.equals("is") || command.equals("iv")))) {
			if (spatial) {
				switch (command) {
				case "co":
					condition = SpatialDSL.contains(field, SpatialDSL.geomFromText(DSL.val(parts[2])));
					break;
				case "cr":
					condition = SpatialDSL.crosses(field, SpatialDSL.geomFromText(DSL.val(parts[2])));
					break;
				case "di":
					condition = SpatialDSL.disjoint(field, SpatialDSL.geomFromText(DSL.val(parts[2])));
					break;
				case "eq":
					condition = SpatialDSL.equals(field, SpatialDSL.geomFromText(DSL.val(parts[2])));
					break;
				case "in":
					condition = SpatialDSL.intersects(field, SpatialDSL.geomFromText(DSL.val(parts[2])));
					break;
				case "ov":
					condition = SpatialDSL.overlaps(field, SpatialDSL.geomFromText(DSL.val(parts[2])));
					break;
				case "to":
					condition = SpatialDSL.touches(field, SpatialDSL.geomFromText(DSL.val(parts[2])));
					break;
				case "wi":
					condition = SpatialDSL.within(field, SpatialDSL.geomFromText(DSL.val(parts[2])));
					break;
				case "ic":
					condition = SpatialDSL.isClosed(field);
					break;
				case "is":
					condition = SpatialDSL.isSimple(field);
					break;
				case "iv":
					condition = SpatialDSL.isValid(field);
					break;
				}
			} else {
				switch (command) {
				case "cs":
					condition = field.contains(parts[2]);
					break;
				case "sw":
					condition = field.startsWith(parts[2]);
					break;
				case "ew":
					condition = field.endsWith(parts[2]);
					break;
				case "eq":
					condition = field.eq(parts[2]);
					break;
				case "lt":
					condition = field.lt(parts[2]);
					break;
				case "le":
					condition = field.le(parts[2]);
					break;
				case "ge":
					condition = field.ge(parts[2]);
					break;
				case "gt":
					condition = field.gt(parts[2]);
					break;
				case "bt":
					parts2 = parts[2].split(",", 2);
					condition = field.between(parts2[0], parts2[1]);
					break;
				case "in":
					parts2 = parts[2].split(",");
					condition = field.in((Object[]) parts2);
					break;
				case "is":
					condition = field.isNull();
					break;
				}
			}
		}
		if (condition != null) {
			if (negate) {
				condition = DSL.not(condition);
			}
		}
		return condition;
	}
}
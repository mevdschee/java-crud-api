package com.tqdev.crudapi.service;

import java.util.ArrayList;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.tqdev.crudapi.spatial.SpatialDSL;

public interface JooqConditions {

	default public ArrayList<Condition> conditions(Params params) {
		ArrayList<Condition> conditions = new ArrayList<>();
		if (params.containsKey("filter")) {
			for (String key : params.get("filter")) {
				String[] parts2;
				String[] parts = key.split(",", 3);
				if (parts.length >= 2) {
					Condition condition = null;
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
					if (parts.length == 3 || (parts.length == 2
							&& (command.equals("ic") || command.equals("is") || command.equals("iv")))) {
						if (spatial) {
							switch (command) {
							case "co":
								condition = SpatialDSL.contains(DSL.field(DSL.name(parts[0])),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "cr":
								condition = SpatialDSL.crosses(DSL.field(DSL.name(parts[0])),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "di":
								condition = SpatialDSL.disjoint(DSL.field(DSL.name(parts[0])),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "eq":
								condition = SpatialDSL.equals(DSL.field(DSL.name(parts[0])),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "in":
								condition = SpatialDSL.intersects(DSL.field(DSL.name(parts[0])),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "ov":
								condition = SpatialDSL.overlaps(DSL.field(DSL.name(parts[0])),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "to":
								condition = SpatialDSL.touches(DSL.field(DSL.name(parts[0])),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "wi":
								condition = SpatialDSL.within(DSL.field(DSL.name(parts[0])),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "ic":
								condition = SpatialDSL.isClosed(DSL.field(DSL.name(parts[0])));
								break;
							case "is":
								condition = SpatialDSL.isSimple(DSL.field(DSL.name(parts[0])));
								break;
							case "iv":
								condition = SpatialDSL.isValid(DSL.field(DSL.name(parts[0])));
								break;
							}
						} else {
							switch (command) {
							case "cs":
								condition = DSL.field(DSL.name(parts[0])).contains(parts[2]);
								break;
							case "sw":
								condition = DSL.field(DSL.name(parts[0])).startsWith(parts[2]);
								break;
							case "ew":
								condition = DSL.field(DSL.name(parts[0])).endsWith(parts[2]);
								break;
							case "eq":
								condition = DSL.field(DSL.name(parts[0])).eq(parts[2]);
								break;
							case "lt":
								condition = DSL.field(DSL.name(parts[0])).lt(parts[2]);
								break;
							case "le":
								condition = DSL.field(DSL.name(parts[0])).le(parts[2]);
								break;
							case "ge":
								condition = DSL.field(DSL.name(parts[0])).ge(parts[2]);
								break;
							case "gt":
								condition = DSL.field(DSL.name(parts[0])).gt(parts[2]);
								break;
							case "bt":
								parts2 = parts[2].split(",", 2);
								condition = DSL.field(DSL.name(parts[0])).between(parts2[0], parts2[1]);
								break;
							case "in":
								parts2 = parts[2].split(",");
								condition = DSL.field(DSL.name(parts[0])).in((Object[]) parts2);
								break;
							case "is":
								condition = DSL.field(DSL.name(parts[0])).isNull();
								break;
							}
						}
					}
					if (condition != null) {
						if (negate) {
							condition = condition.not();
						}
						conditions.add(condition);
					}
				}
			}
		}
		return conditions;
	}

}
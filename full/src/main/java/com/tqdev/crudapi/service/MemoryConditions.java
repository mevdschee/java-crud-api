package com.tqdev.crudapi.service;

import org.jooq.impl.DSL;

import com.tqdev.crudapi.service.record.MemoryRecord;
import com.tqdev.crudapi.spatial.SpatialDSL;

public interface MemoryConditions {

	default public boolean matchesConditions(MemoryRecord record, Params params) {
		int i, s, e;
		boolean result = true;
		if (params.containsKey("filter")) {
			for (String key : params.get("filter")) {
				String[] parts2;
				String[] parts = key.split(",", 3);
				if (record.containsKey(parts[0]) && parts.length >= 2) {
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
								condition = SpatialDSL.contains(DSL.field(parts[0]),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "cr":
								condition = SpatialDSL.crosses(DSL.field(parts[0]),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "di":
								condition = SpatialDSL.disjoint(DSL.field(parts[0]),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "eq":
								condition = SpatialDSL.equals(DSL.field(parts[0]),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "in":
								condition = SpatialDSL.intersects(DSL.field(parts[0]),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "ov":
								condition = SpatialDSL.overlaps(DSL.field(parts[0]),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "to":
								condition = SpatialDSL.touches(DSL.field(parts[0]),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "wi":
								condition = SpatialDSL.within(DSL.field(parts[0]),
										SpatialDSL.geomFromText(DSL.val(parts[2])));
								break;
							case "ic":
								condition = SpatialDSL.isClosed(DSL.field(parts[0]));
								break;
							case "is":
								condition = SpatialDSL.isSimple(DSL.field(parts[0]));
								break;
							case "iv":
								condition = SpatialDSL.isValid(DSL.field(parts[0]));
								break;
							}
						} else {
							switch (command) {
							case "cs":
								result = (String.valueOf(record.get(parts[0]))).contains(parts[2]);
								break;
							case "sw":
								result = (String.valueOf(record.get(parts[0]))).startsWith(parts[2]);
								break;
							case "ew":
								result = (String.valueOf(record.get(parts[0]))).endsWith(parts[2]);
								break;
							case "eq":
								// switch type?
								result = (String.valueOf(record.get(parts[0]))).equals(parts[2]);
								break;
							case "lt":
								i = Integer.valueOf(String.valueOf(parts[0]));
								s = Integer.valueOf(String.valueOf(parts[2]));
								result = i < s;
								break;
							case "le":
								i = Integer.valueOf(String.valueOf(parts[0]));
								s = Integer.valueOf(String.valueOf(parts[2]));
								result = i <= s;
								break;
							case "ge":
								i = Integer.valueOf(String.valueOf(parts[0]));
								s = Integer.valueOf(String.valueOf(parts[2]));
								result = i >= s;
								break;
							case "gt":
								i = Integer.valueOf(String.valueOf(parts[0]));
								s = Integer.valueOf(String.valueOf(parts[2]));
								result = i > s;
								break;
							case "bt":
								parts2 = parts[2].split(",", 2);
								i = Integer.valueOf(String.valueOf(parts[0]));
								s = Integer.valueOf(String.valueOf(parts2[0]));
								e = Integer.valueOf(String.valueOf(parts2[0]));
								result = i > s && i < e;
								break;
							case "in":
								String val = String.valueOf(record.get(parts[0]));
								for (String part : parts[2].split(",")) {
									if (part.equals(val)) {
										result = true;
									}
								}
								break;
							case "is":
								result = record.get(parts[0]) == null;
								break;
							}
						}
					}
					if (negate) {
						result = !result;
					}
				}
			}
		}
		return result;
	}

}
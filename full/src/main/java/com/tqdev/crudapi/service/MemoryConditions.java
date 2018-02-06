package com.tqdev.crudapi.service;

import com.tqdev.crudapi.service.record.MemoryRecord;

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
					if (parts.length == 3 || (parts.length == 2 && command.equals("iv"))) {
						if (!spatial) {
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
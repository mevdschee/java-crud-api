package com.tqdev.crudapi.record;

public interface CrudApiHandlers {

	default boolean tableAuthorizer(String command, String database, String table) {
		return true;
	}

	default boolean recordFilter(String command, String database, String table) {
		return false;
	}

	default boolean columnAuthorizer(String command, String database, String table, String column) {
		return true;
	}

	default Object tenancyFunction(String command, String database, String table, String column) {
		return null;
	}

	default Object inputSanitizer(String command, String database, String table, String column, String type,
			String value) {
		return null;
	}

	default boolean inputValidator(String command, String database, String table, String column, String type,
			String value, Object context) {
		return true;
	}

	default void before(String command, String database, String table, String id, Object in) {

	}

	default void after(String command, String database, String table, String id, Object in, Object out) {

	}
}

package com.tqdev.crudapi.openapi;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tqdev.crudapi.column.definition.ColumnDefinition;
import com.tqdev.crudapi.column.reflection.DatabaseReflection;
import com.tqdev.crudapi.column.reflection.ReflectedTable;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.DefaultDataType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Set;

public class OpenApiBuilder {
	private OpenApiDefinition openapi;
	private DatabaseReflection reflection;
	private LinkedHashMap<String,String> operations;
	private LinkedHashMap<String,LinkedHashMap<String,String>> types;

    private LinkedHashMap<String,String> createType(String type, String format) {
        LinkedHashMap<String,String> item = new LinkedHashMap<>();
        item.put("type",type);
        if (format!=null) {
            item.put("format", format);
        }
        return item;
    }

	public OpenApiBuilder(DatabaseReflection reflection, OpenApiDefinition base)
	{
		operations = new LinkedHashMap<>();
		operations.put("list", "getTable");
		operations.put("create", "post");
		operations.put("read", "getTable");
		operations.put("update", "put");
		operations.put("delete", "delete");
		operations.put("increment", "patch");
        types = new LinkedHashMap<>();
        types.put("integer",createType("integer","int32"));
        types.put("bigint",createType("integer","int64"));
        types.put("varchar",createType("string",null));
        types.put("clob",createType("string",null));
        types.put("varbinary",createType("string","byte"));
        types.put("blob",createType("string","byte"));
        types.put("decimal",createType("string",null));
        types.put("float",createType("number","float"));
        types.put("double",createType("number","double"));
        types.put("time",createType("string","date-time"));
        types.put("timestamp",createType("string","date-time"));
        types.put("geometry",createType("string",null));
        types.put("boolean",createType("boolean",null));
        this.reflection = reflection;
		openapi = new OpenApiDefinition(base);
	}

	public OpenApiDefinition build()
	{
		openapi.set("openapi", "3.0.0");
		Set<String> tableNames = reflection.getTableNames();
		for (String tableName: tableNames) {
		    setPath(tableName);
	    }
		openapi.set("components|responses|pk_integer|description", "inserted primary key value (integer)");
		openapi.set("components|responses|pk_integer|content|application/json|schema|type", "integer");
		openapi.set("components|responses|pk_integer|content|application/json|schema|format", "int64");
		openapi.set("components|responses|pk_string|description", "inserted primary key value (string)");
		openapi.set("components|responses|pk_string|content|application/json|schema|type", "string");
		openapi.set("components|responses|pk_string|content|application/json|schema|format", "uuid");
		openapi.set("components|responses|rows_affected|description", "number of rows affected (integer)");
		openapi.set("components|responses|rows_affected|content|application/json|schema|type", "integer");
		openapi.set("components|responses|rows_affected|content|application/json|schema|format", "int64");
        for (String tableName: tableNames) {
            setComponentSchema(tableName);
            setComponentResponse(tableName);
            setComponentRequestBody(tableName);
        }
		setComponentParameters();
        int i=0;
        for (String tableName: tableNames) {
			setTag(i, tableName);
    		i++;
	    }
		return openapi;
	}

	private boolean isOperationOnTableAllowed(String operation, String tableName)
	{
		/*tableHandler = VariableStore.getTable("authorization.tableHandler");
		if (tableHandler) {
			return true;
		}
		return (bool) call_user_func($tableHandler, $operation, $tableName);*/
		return true;
	}

	private boolean isOperationOnColumnAllowed(String operation, String tableName, String columnName)
	{
		/*$columnHandler = VariableStore::getTable("authorization.columnHandler");
		if (!$columnHandler) {
			return true;
		}
		return (bool) call_user_func($columnHandler, $operation, $tableName, $columnName);*/
        return true;
	}

	private String urlencode(String str){
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

	private void setPath(String tableName)
	{
		ReflectedTable table = reflection.getTable(tableName);
		String type = table.getType();
		Field<?> pk = table.getPk();
		String pkName = pk!=null ? pk.getName() : null;
		String path;
		for (String operation : operations.keySet()) {
		    String method = operations.get(operation);
            if (pkName==null && !operation.equals("list")) {
                continue;
            }
            if (!type.equals("table") && !operation.equals("list")) {
                continue;
            }
            if (!isOperationOnTableAllowed(operation, tableName)) {
                continue;
            }
            if (operation.equals("list") || operation.equals("create")) {
                path = String.format("/records/%s", tableName);
            } else {
                path = String.format("/records/%s/{%s}", tableName, pkName);
                openapi.set(String.format("paths|%s|%s|parameters|0|\\$ref",path,method), "#/components/parameters/pk");
            }
            if (operation.equals("create") || operation.equals("update") || operation.equals("increment")) {
                openapi.set(String.format("paths|%s|%s|requestBody|\\$ref",path,method), String.format("#/components/requestBodies/%s-%s",operation,urlencode(tableName)));
            }
            openapi.set(String.format("paths|%s|%s|tags|0",path,method), tableName);
            openapi.set(String.format("paths|%s|%s|description",path,method), String.format("%s %s",operation, tableName));
            switch (operation) {
                case "list":
                    openapi.set(String.format("paths|%s|%s|responses|200|\\$ref",path,method), String.format("#/components/responses/%s-%s",operation,urlencode(tableName)));
                    break;
                case "create":
                    if (pk.getType().equals("integer")) {
                        openapi.set(String.format("paths|%s|%s|responses|200|\\$ref",path,method), "#/components/responses/pk_integer");
                    } else {
                        openapi.set(String.format("paths|%s|%s|responses|200|\\$ref",path,method), "#/components/responses/pk_string");
                    }
                    break;
                case "read":
                    openapi.set(String.format("paths|%s|%s|responses|200|\\$ref",path,method), String.format("#/components/responses/%s-%s",operation,urlencode(tableName)));
                    break;
                case "update":
                case "delete":
                case "increment":
                    openapi.set(String.format("paths|%s|%s|responses|200|\\$ref",path,method), "#/components/responses/rows_affected");
                    break;
            }
        }
	}

	private void setComponentSchema(String tableName) {
        ReflectedTable table = reflection.getTable(tableName);
        String type = table.getType();
        Field<?> pk = table.getPk();
        String pkName = pk != null ? pk.getName() : null;
        String prefix;
        for (String operation : operations.keySet()) {
            String method = operations.get(operation);
            if (pkName==null && !operation.equals("list")) {
                continue;
            }
            if (!type.equals("table") && !operation.equals("list")) {
                continue;
            }
            if (operation.equals("delete")) {
                continue;
            }
            if (!isOperationOnTableAllowed(operation, tableName)) {
                continue;
            }
            if (operation.equals("list")) {
                openapi.set(String.format("components|schemas|%s-%s|type",operation,tableName), "object");
                openapi.set(String.format("components|schemas|%s-%s|properties|results|type",operation,tableName), "integer");
                openapi.set(String.format("components|schemas|%s-%s|properties|results|format",operation,tableName), "int64");
                openapi.set(String.format("components|schemas|%s-%s|properties|records|type",operation,tableName), "array");
                prefix = String.format("components|schemas|%s-%s|properties|records|items",operation,tableName);
            } else {
                prefix = String.format("components|schemas|%s-%s",operation,tableName);
            }
            openapi.set(String.format("%s|type",prefix), "object");
            for(String columnName : table.fieldNames()) {
                if (!isOperationOnColumnAllowed(operation, tableName, columnName)) {
                    continue;
                }
                ColumnDefinition column = new ColumnDefinition(table.get(columnName));
                LinkedHashMap<String, String> properties = types.get(column.getType());
                if (properties==null) {
                    properties = new LinkedHashMap<>();
                    properties.put("type","string");
                }
                for (String key : properties.keySet()) {
                    String value = properties.get(key);
                    openapi.set(String.format("%s|properties|%s|%s",prefix,columnName,key), value);
                }
            }
        }
    }

	private void setComponentResponse(String tableName)
	{
        ReflectedTable table = reflection.getTable(tableName);
        String type = table.getType();
        Field<?> pk = table.getPk();
        String pkName = pk != null ? pk.getName() : null;
        for (String operation : new String[]{"list", "read"}) {
            if (pkName==null && !operation.equals("list")) {
                continue;
            }
            if (!type.equals("table") && !operation.equals("list")) {
                continue;
            }
            if (!isOperationOnTableAllowed(operation, tableName)) {
                continue;
            }
            if (operation.equals("list")) {
                openapi.set(String.format("components|responses|%s-%s|description",operation,tableName), String.format("list of %s records",tableName));
            } else {
                openapi.set(String.format("components|responses|%s-%s|description",operation,tableName), String.format("single %s record",tableName));
            }
            openapi.set(String.format("components|responses|%s-%s|content|application/json|schema|\\$ref",operation,tableName), String.format("#/components/schemas/%s-%s",operation,urlencode(tableName)));
        }
	}

	private void setComponentRequestBody(String tableName)
	{
        ReflectedTable table = reflection.getTable(tableName);
        String type = table.getType();
        Field<?> pk = table.getPk();
        String pkName = pk != null ? pk.getName() : null;
        if (pkName!=null && type.equals("table")) {
            for (String operation : new String[]{"create", "update", "increment"}) {
				if (!isOperationOnTableAllowed(operation, tableName)) {
					continue;
				}
				openapi.set(String.format("components|requestBodies|%s-%s|description",operation,tableName), String.format("single %s record",tableName));
				openapi.set(String.format("components|requestBodies|%s-%s|content|application/json|schema|\\$ref",operation,tableName), String.format("#/components/schemas/%s-%s",operation,urlencode(tableName)));
			}
		}
	}

	private void setComponentParameters()
	{
		openapi.set("components|parameters|pk|name", "id");
		openapi.set("components|parameters|pk|in", "path");
		openapi.set("components|parameters|pk|schema|type", "string");
		openapi.set("components|parameters|pk|description", "primary key value");
		openapi.set("components|parameters|pk|required", true);
	}

	private void setTag(int index, String tableName)
	{
		openapi.set(String.format("tags|%d|name",index), tableName);
		openapi.set(String.format("tags|%d|description",index), String.format("%s operations",tableName));
	}
}

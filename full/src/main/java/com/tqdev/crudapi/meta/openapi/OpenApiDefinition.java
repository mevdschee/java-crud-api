package com.tqdev.crudapi.meta.openapi;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.jdbc.StringUtils;
import com.tqdev.crudapi.meta.definition.ColumnDefinition;
import com.tqdev.crudapi.meta.definition.DatabaseDefinition;
import com.tqdev.crudapi.meta.definition.TableDefinition;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class OpenApiDefinition {

    protected ObjectNode root;

    public OpenApiDefinition() {
        ObjectMapper mapper = new ObjectMapper();
        root = mapper.createObjectNode();
    }

    public OpenApiDefinition(OpenApiDefinition copy) {
        root = copy.root.deepCopy();
    }

    public static OpenApiDefinition fromFile(String filename)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(filename);
        OpenApiDefinition result;
        try {
            result = mapper.readValue(resource.getInputStream(), OpenApiDefinition.class);
        } catch (FileNotFoundException e) {
            result = null;
        }
        return result;
    }

    @JsonAnyGetter
    public Map<String, JsonNode> any() {
        LinkedHashMap<String, JsonNode> result = new LinkedHashMap<>();
        Iterator<Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> field = fields.next();
            result.put(field.getKey(), field.getValue());
        }
        return result;
    }

    @JsonAnySetter
    public void set(String name, JsonNode value) {
        root.set(name, value);
    }

    public void inject(DatabaseDefinition database) {

        for (TableDefinition table : database.getTables()) {
            String path = "/data/" + table.getName();
            ObjectNode paths = root.with("paths");
            ObjectNode pathItem = paths.with(path);

            for (String method : new String[]{"get", "post", "put", "patch", "delete"}) {
                ObjectNode operation = pathItem.with(method);
                ObjectNode okResponse = operation.with("responses").with("200");
                okResponse.put("description", method + " operation");
                fillContentResponse(table, okResponse);
                fillParametersWithPrimaryKey(method, table, paths);
            }


        }
    }

    private void fillParametersWithPrimaryKey(String method, TableDefinition table, ObjectNode paths) {
        if (!StringUtils.isNullOrEmpty(table.getPk())) {

            String pathWithId = String.format("/data/%s/{%s}", table.getName(), table.getPk());
            ObjectNode pathItemId = paths.with(pathWithId);
            ObjectNode operation = pathItemId.with(method);

            ObjectNode okResponse = operation.with("responses").with("200");
            okResponse.put("description", method + " operation");

            fillResponseParameters(table, operation);

        }
    }

    /**
        post:
        responses:
                '200':
        description: post operation
        parameters:
                - name: actor_id
        in: path
        required: true
        schema:
        type: string
     **/
    private void fillResponseParameters(TableDefinition table, ObjectNode operation) {
        ArrayNode parameters = operation.putArray("parameters");

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("name", table.getPk());
        node.put("in", "path");
        node.put("required", true);
        ObjectNode schema = node.with("schema");
        schema.put("type", decodeType(table.getPkColumnDefinition()) );

        parameters.add(node);
    }

    /**
    content:  # Response body
    application/json:  # Media type
      schema:          # Must-have
        type: object   # Data type
        properties:
          id:
            type: integer
          name:
            type: string
          fullTime:
            type: boolean
        example:       # Sample data
            id: 1
            name: Jessica Right
            fullTime: true
     **/
    private void fillContentResponse(TableDefinition table, ObjectNode okResponse) {
        ObjectNode content = okResponse.with("content");
        ObjectNode json = content.with("application/json");
        ObjectNode schema = json.with("schema");
        schema.put("type", "object");
        ObjectNode properties = schema.with("properties");

        Collection<ColumnDefinition> columns = table.getColumns();
        for (ColumnDefinition columnDefinition : columns){
            ObjectNode col = properties.with(columnDefinition.getName());
            col.put("type", decodeType(columnDefinition));
        }

        ObjectNode example = schema.with("example");
        for (ColumnDefinition columnDefinition : columns){
            String decodeType = decodeType(columnDefinition);
            example.put(columnDefinition.getName(), decodeTypeForDefaultValue(decodeType) );
        }

    }


    /**
     * According to
     * https://swagger.io/docs/specification/data-models/data-types/
     * @param columnDefinition
     * @return
     */

    private String decodeType(ColumnDefinition columnDefinition){
        String type = "string";
        switch (columnDefinition.getType()){
            case "varchar":
            case "varchar2":
            case "clob":
            case "timestamp":
            case "date":
                type = "string";
                break;
            case "smallint unsigned":
            case "smallint":
                type = "integer";
                break;
            case "float":
                type = "float";
                break;
            case "tinyint":
            case "bit":
                type = "boolean";
            default:
                type = "string";
        }
        return type;
    }

    /*
    string (this includes dates and files)
    number
    integer
    boolean
    array
    object
     */
    private String decodeTypeForDefaultValue(String type){
        switch (type){
            case "string":
                type = "string";
                break;
            case "integer":
                type = "1";
                break;
            case "boolean":
                type = "true";
                break;
            case "array":
                type = "[]";
            default:
                type = "";
        }
        return type;
    }

}

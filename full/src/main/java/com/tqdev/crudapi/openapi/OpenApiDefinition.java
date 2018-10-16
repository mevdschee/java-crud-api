package com.tqdev.crudapi.openapi;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpenApiDefinition {

    private ObjectNode root;

    public ObjectNode getRoot()
    {
        return root;
    }

    public OpenApiDefinition()
    {
        ObjectMapper mapper = new ObjectMapper();
        root = mapper.createObjectNode();
    }

    public OpenApiDefinition(OpenApiDefinition copy)
    {
        root = copy.getRoot().deepCopy();
    }

    @JsonAnyGetter
    public Map<String, JsonNode> jsonAnyGet() {
        LinkedHashMap<String, JsonNode> result = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            result.put(field.getKey(), field.getValue());
        }
        return result;
    }

    @JsonAnySetter
    public void jsonAnySet(String name, JsonNode value) {
        root.set(name, value);
    }

    public static OpenApiDefinition fromFile(String filename) throws IOException {
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

    public void set(String path, Object value)
    {
        String[] parts = path.replaceAll("\\|$|^\\|", "").split("\\|");
        ObjectNode current = root;
        for (int i=0;i<parts.length-1;i++) {
            String part = parts[i];
            current = current.with(part);
        }
        if (value instanceof Boolean) {
            current.put(parts[parts.length - 1], (Boolean) value);
        } else {
            current.put(parts[parts.length - 1], value.toString());
        }
    }

}

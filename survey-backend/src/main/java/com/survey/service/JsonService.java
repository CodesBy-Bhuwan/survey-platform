
package com.survey.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import com.survey.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class JsonService {

    private final JsonMapper jsonMapper;

    public JsonService(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public String write(JsonNode node) {
        try {
            return jsonMapper.writeValueAsString(node);
        } catch (JacksonException ex) {
            throw new BadRequestException(
                    "Could not process JSON data."
            );
        }
    }

    public JsonNode read(String json) {
        try {
            return jsonMapper.readTree(json);
        } catch (JacksonException ex) {
            throw new BadRequestException(
                    "Stored JSON data is invalid."
            );
        }
    }
}

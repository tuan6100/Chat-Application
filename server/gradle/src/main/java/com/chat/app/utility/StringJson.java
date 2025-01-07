package com.chat.app.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class StringJson<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;

    public StringJson(Class<T> type) {
        this.type = type;
    }

    public List<T> fromJson(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize message responses", e);
        }
    }

    public String toJson(List<T> messageResponses) {
        try {
            return objectMapper.writeValueAsString(messageResponses);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message responses", e);
        }
    }
}
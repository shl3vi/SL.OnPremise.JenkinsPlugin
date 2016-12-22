package io.sealights.plugins.sealightsjenkins.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonSerializer {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object target, boolean prettyPrint) {
        try {
            if (prettyPrint) {
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            } else {
                objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            }

            return objectMapper.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object. Error:", e);
        }
    }

    public static String serialize(Object target) {
        return serialize(target, false);
    }

    public static <T> T deserialize(String json, Class<T> targetType) {
        T deserializedObject = null;
        try {
            deserializedObject = objectMapper.readValue(json, targetType);
        } catch (JsonParseException | JsonMappingException e) {
            throw new RuntimeException("Failed during JSON deserialization. Error: " + e.toString(), e);
        } catch (IOException e) {
            throw new RuntimeException("Failed during JSON deserialization. Error: " + e.toString(), e);
        }

        return deserializedObject;
    }

    public static <T1, T2> Map<T1, T2> deserializeMap(String json) {
        Map<T1, T2> deserializedObject = null;
        try {
            deserializedObject = objectMapper.readValue(json, new TypeReference<Map<T1, T2>>() {
            });
        } catch (JsonParseException | JsonMappingException e) {
            throw new RuntimeException("Failed during JSON deserialization. Error: " + e.toString(), e);
        } catch (IOException e) {
            throw new RuntimeException("Failed during JSON deserialization. Error: " + e.toString(), e);
        }

        return deserializedObject;
    }

    public static <T> T deserialize(File file, Class<T> targetType) {
        T deserializedObject = null;
        try {
            deserializedObject = objectMapper.readValue(file, targetType);
        } catch (JsonParseException | JsonMappingException e) {
            throw new RuntimeException("Failed during JSON deserialization. Error: " + e.toString(), e);
        } catch (IOException e) {
            throw new RuntimeException("Failed during JSON deserialization. Error: " + e.toString(), e);
        }

        return deserializedObject;
    }
}
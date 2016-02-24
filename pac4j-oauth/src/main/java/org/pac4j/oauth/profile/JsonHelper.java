package org.pac4j.oauth.profile;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is an helper to work with JSON.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class JsonHelper {

    private static final Logger logger = LoggerFactory.getLogger(JsonHelper.class);

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Return the first node of a JSON response.
     *
     * @param text JSON text
     * @return the first node of the JSON response or null if exception is thrown
     */
    public static JsonNode getFirstNode(final String text) {
        try {
            return mapper.readValue(text, JsonNode.class);
        } catch (final IOException e) {
            logger.error("Cannot get first node", e);
        }
        return null;
    }

    /**
     * Return an Object from a JSON node.
     *
     * @param node a JSON node
     * @param clazz the class to cast to
     * @param <T> the class to cast to
     * @return the parsed object
     */
    public static <T extends Object> T getAsType(final JsonNode node, final Class<T> clazz) {
        try {
            return mapper.treeToValue(node, clazz);
        } catch (final IOException e) {
            logger.error("Cannot get as type", e);
        }
        return null;
    }

    /**
     * Return the field with name in JSON as a string, a boolean, a number or a node.
     *
     * @param json json
     * @param name node name
     * @return the field
     */
    public static Object getElement(final JsonNode json, final String name) {
        if (json != null && name != null) {
            JsonNode node = json;
            for (String nodeName : name.split("\\.")) {
                if (node != null) {
                    node = node.get(nodeName);
                }
            }
            if (node != null) {
                if (node.isNumber()) {
                    return node.numberValue();
                } else if (node.isBoolean()) {
                    return node.booleanValue();
                } else if (node.isTextual()) {
                    return node.textValue();
                } else if (node.isNull()) {
                    return null;
                } else {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Returns the JSON string for the object.
     *
     * @param obj the object
     * @return the JSON string
     */
    public static String toJSONString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (final JsonProcessingException e) {
            logger.error("Cannot to JSON string", e);
        }
        return null;
    }
}

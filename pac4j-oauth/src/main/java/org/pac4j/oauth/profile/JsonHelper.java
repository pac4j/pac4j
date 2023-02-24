package org.pac4j.oauth.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * This class is an helper to work with JSON.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
@Slf4j
public final class JsonHelper {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonHelper() {}

    /**
     * Return the first node of a JSON response.
     *
     * @param text JSON text
     * @return the first node of the JSON response or null if exception is thrown
     */
    public static JsonNode getFirstNode(final String text) {
        return getFirstNode(text, null);
    }

    /**
     * Return the first node of a JSON response.
     *
     * @param text JSON text
     * @param path path to find the first node
     * @return the first node of the JSON response or null if exception is thrown
     */
    public static JsonNode getFirstNode(final String text, final String path) {
        try {
            var node = mapper.readValue(text, JsonNode.class);
            if (path != null) {
                node = (JsonNode) getElement(node, path);
            }
            return node;
        } catch (final IOException e) {
            LOGGER.error("Cannot get first node", e);
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
            var node = json;
            for (var nodeName : name.split("\\.")) {
                if (node != null) {
                    if (nodeName.matches("\\d+")) {
                        node = node.get(Integer.parseInt(nodeName));
                    } else {
                        node = node.get(nodeName);
                    }
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
            LOGGER.error("Cannot to JSON string", e);
        }
        return null;
    }

    /**
     * <p>Getter for the field <code>mapper</code>.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.ObjectMapper} object
     */
    public static ObjectMapper getMapper() {
        return mapper;
    }
}

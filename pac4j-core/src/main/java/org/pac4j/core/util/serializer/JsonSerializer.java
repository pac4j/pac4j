package org.pac4j.core.util.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * A JSON serializer.
 *
 * @author Jerome LELEU
 * @since 3.9.0
 */
public class JsonSerializer implements Serializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper;

    private Class<? extends Object> clazz;

    public JsonSerializer(final Class<? extends Object> clazz) {
        assertNotNull("clazz", clazz);
        this.clazz = clazz;

        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public String encode(final Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Cannot encode object", e);
            return null;
        }
    }

    @Override
    public Object decode(final String encoded) {
        if (encoded == null) {
            return null;
        }
        try {
            return objectMapper.readValue(encoded, clazz);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Cannot decode string", e);
            return null;
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        assertNotNull("objectMapper", objectMapper);
        this.objectMapper = objectMapper;
    }
}

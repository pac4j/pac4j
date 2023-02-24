package org.pac4j.core.util.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * A JSON serializer.
 *
 * @author Jerome LELEU
 * @since 3.9.0
 */
@Slf4j
public class JsonSerializer extends AbstractSerializer {

    private ObjectMapper objectMapper;

    private Class<? extends Object> clazz;

    /**
     * <p>Constructor for JsonSerializer.</p>
     *
     * @param clazz a {@link java.lang.Class} object
     */
    public JsonSerializer(final Class<? extends Object> clazz) {
        assertNotNull("clazz", clazz);
        this.clazz = clazz;

        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    /** {@inheritDoc} */
    @Override
    protected String internalSerializeToString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Cannot encode object", e);
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Object internalDeserializeFromString(final String encoded) {
        try {
            return objectMapper.readValue(encoded, clazz);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Cannot decode string", e);
            return null;
        }
    }

    /**
     * <p>Getter for the field <code>objectMapper</code>.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.ObjectMapper} object
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * <p>Setter for the field <code>objectMapper</code>.</p>
     *
     * @param objectMapper a {@link com.fasterxml.jackson.databind.ObjectMapper} object
     */
    public void setObjectMapper(final ObjectMapper objectMapper) {
        assertNotNull("objectMapper", objectMapper);
        this.objectMapper = objectMapper;
    }
}

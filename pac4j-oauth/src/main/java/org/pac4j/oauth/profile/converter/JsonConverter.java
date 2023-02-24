package org.pac4j.oauth.profile.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.JsonHelper;

import java.io.IOException;

/**
 * This class converts a JSON node (or string) into an object.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@Slf4j
public final class JsonConverter implements AttributeConverter {

    private final Class<? extends Object> clazz;

    private TypeReference<? extends Object> typeReference;

    /**
     * <p>Constructor for JsonConverter.</p>
     *
     * @param clazz a {@link java.lang.Class} object
     */
    public JsonConverter(final Class<? extends Object> clazz) {
        this.clazz = clazz;
    }

    /**
     * <p>Constructor for JsonConverter.</p>
     *
     * @param clazz a {@link java.lang.Class} object
     * @param typeReference a {@link com.fasterxml.jackson.core.type.TypeReference} object
     */
    public JsonConverter(final Class<? extends Object> clazz, final TypeReference<? extends Object> typeReference) {
        this(clazz);
        this.typeReference = typeReference;
    }

    /** {@inheritDoc} */
    @Override
    public Object convert(final Object attribute) {
        if (attribute != null) {
            if (clazz.isAssignableFrom(attribute.getClass())) {
                return attribute;
            } else if (attribute instanceof String || attribute instanceof JsonNode) {
                final String s;
                if (attribute instanceof String) {
                    s = (String) attribute;
                } else {
                    s = JsonHelper.toJSONString(attribute);
                }

                try {
                    if (typeReference != null) {
                        return JsonHelper.getMapper().readValue(s, typeReference);
                    } else {
                        return JsonHelper.getMapper().readValue(s, clazz);
                    }
                } catch (final IOException e) {
                    LOGGER.error("Cannot read value", e);
                }
            }
        }
        return null;
    }
}

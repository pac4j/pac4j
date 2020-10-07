package org.pac4j.oauth.profile.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class converts a JSON node (or string) into an object.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class JsonConverter implements AttributeConverter {

    private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);

    private final Class<? extends Object> clazz;

    private TypeReference<? extends Object> typeReference;

    public JsonConverter(final Class<? extends Object> clazz) {
        this.clazz = clazz;
    }

    public JsonConverter(final Class<? extends Object> clazz, final TypeReference<? extends Object> typeReference) {
        this(clazz);
        this.typeReference = typeReference;
    }

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
                    logger.error("Cannot read value", e);
                }
            }
        }
        return null;
    }
}

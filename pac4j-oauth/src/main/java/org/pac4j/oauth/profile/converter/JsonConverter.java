package org.pac4j.oauth.profile.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.JsonHelper;

/**
 * This class converts a JSON node (or string) into an object.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class JsonConverter<T extends Object> implements AttributeConverter<T> {

    private final Class<T> clazz;

    public JsonConverter(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public T convert(final Object attribute) {
        if (attribute != null) {
            if (attribute.getClass().isAssignableFrom(clazz)) {
                return (T) attribute;
            } else if (attribute instanceof String) {
                final JsonNode node = JsonHelper.getFirstNode((String) attribute);
                return JsonHelper.getAsType(node, clazz);
            } else if (attribute instanceof JsonNode) {
                final T ret = JsonHelper.getAsType((JsonNode) attribute, clazz);
                return ret;
            }
        }
        return null;
    }
}

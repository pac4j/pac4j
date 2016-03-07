package org.pac4j.oauth.profile.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.JsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This class converts a JSON node, a JSON string or a list of JSON strings into a list of objects.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class JsonListConverter implements AttributeConverter<Object> {

    private final Class arrayClazz;

    private final Class elementClazz;

    public JsonListConverter(final Class elementClazz, final Class  arrayClazz) {
        this.elementClazz = elementClazz;
        this.arrayClazz = arrayClazz;
    }

    public Object convert(final Object attribute) {
        if (attribute != null) {
            if (attribute instanceof String) {
                return parseString((String) attribute, arrayClazz);
            } else if (attribute instanceof JsonNode) {
                return JsonHelper.getAsType((JsonNode) attribute, arrayClazz);
            } else if (attribute instanceof List) {
                final List list = (List) attribute;
                final List newList = new ArrayList<>();
                for (final Object o : list) {
                    if (o != null) {
                        if (o instanceof String) {
                            final String s = (String) o;
                            if (elementClazz.isAssignableFrom(String.class)) {
                                newList.add(s);
                            } else {
                                newList.add(parseString(s, elementClazz));
                            }
                        } else if (o.getClass().isAssignableFrom(elementClazz)) {
                            newList.add(o);
                        }
                    }
                }
                return newList;
            }
        }
        return null;
    }

    private Object parseString(final String s, final Class type) {
        final JsonNode node = JsonHelper.getFirstNode(s);
        return JsonHelper.getAsType(node, type);
    }
}

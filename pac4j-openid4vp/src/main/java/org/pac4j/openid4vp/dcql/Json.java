package org.pac4j.openid4vp.dcql;

import lombok.experimental.UtilityClass;
import org.pac4j.core.exception.TechnicalException;

import java.util.List;
import java.util.Map;

/**
 * Reading the parsed JSON of a query: the members are typed here, so that a malformed query fails with a
 * message naming the member rather than with a class cast.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
class Json {

    @SuppressWarnings("unchecked")
    Map<String, Object> object(final Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        throw new TechnicalException("a JSON object was expected in the DCQL query, got: " + value);
    }

    List<Object> list(final Map<String, Object> json, final String member) {
        final Object value = json.get(member);
        if (value == null) {
            return List.of();
        }
        if (value instanceof List) {
            return (List<Object>) value;
        }
        throw new TechnicalException("a JSON array was expected for the DCQL member " + member + ", got: " + value);
    }

    List<String> strings(final Object value) {
        if (value instanceof List<?> items) {
            return items.stream().map(item -> {
                if (item instanceof String string) {
                    return string;
                }
                throw new TechnicalException("a string was expected in the DCQL query, got: " + item);
            }).toList();
        }
        throw new TechnicalException("an array of strings was expected in the DCQL query, got: " + value);
    }

    String string(final Map<String, Object> json, final String member) {
        final Object value = json.get(member);
        if (value == null || value instanceof String) {
            return (String) value;
        }
        throw new TechnicalException("a string was expected for the DCQL member " + member + ", got: " + value);
    }

    Boolean bool(final Map<String, Object> json, final String member) {
        final Object value = json.get(member);
        if (value == null || value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new TechnicalException("a boolean was expected for the DCQL member " + member + ", got: " + value);
    }

    /** A path segment: a string, an integer (parsed as a long) or null. */
    Object pathSegment(final Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return value;
    }
}

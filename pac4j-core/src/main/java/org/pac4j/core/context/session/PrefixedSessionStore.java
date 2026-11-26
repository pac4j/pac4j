package org.pac4j.core.context.session;

import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.ProtocolMessages;

/**
 * Session store with prefix.
 *
 * @author Jerome Leleu
 * @since 5.7.2
 */
@Getter
@Setter
public abstract class PrefixedSessionStore implements SessionStore {

    private String prefix = Pac4jConstants.EMPTY_STRING;

    public String computePrefixedKey(final String key) {
        return prefix + key;
    }

    /** Mask private DPoP keys before handing session attribute values to the logger. */
    public static Object valueForLog(final String key, final Object value) {
        if (value != null && key != null && key.startsWith(Pac4jConstants.DPOP_KEY_SESSION_ATTRIBUTE_PREFIX)) {
            return ProtocolMessages.MASK;
        }
        return value instanceof Exception ? value.toString() : value;
    }
}

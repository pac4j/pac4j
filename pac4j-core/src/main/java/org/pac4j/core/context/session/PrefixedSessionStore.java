package org.pac4j.core.context.session;

import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.util.Pac4jConstants;

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

    protected String computePrefixedKey(final String key) {
        return prefix + key;
    }
}

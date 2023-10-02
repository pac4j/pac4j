package org.pac4j.core.context.session;

import org.pac4j.core.util.Pac4jConstants;

/**
 * Session store with prefix.
 *
 * @author Jerome Leleu
 * @since 5.7.2
 */
public abstract class PrefixedSessionStore implements SessionStore {

    private String prefix = Pac4jConstants.EMPTY_STRING;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    protected String computePrefixedKey(final String key) {
        return prefix + key;
    }
}

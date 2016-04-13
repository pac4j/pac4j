package org.pac4j.core.context.session;

import org.pac4j.core.context.WebContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock a session store in memory.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class MockSessionStore implements SessionStore {

    protected Map<String, Object> store = new HashMap<>();

    @Override
    public String getOrCreateSessionId(final WebContext context) {
        return new Date().toString();
    }

    @Override
    public Object get(final WebContext context, final String key) {
        return store.get(key);
    }

    @Override
    public void set(final WebContext context, final String key, final Object value) {
        store.put(key, value);
    }
}

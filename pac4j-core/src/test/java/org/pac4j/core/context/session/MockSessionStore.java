package org.pac4j.core.context.session;

import org.pac4j.core.context.WebContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock a session store in memory.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class MockSessionStore implements SessionStore {

    protected Map<String, Object> store = new HashMap<>();

    protected String id;

    public MockSessionStore() {}

    public MockSessionStore(final Map<String, Object> store) {
        this.store = store;
    }

    @Override
    public Optional<String> getSessionId(final WebContext context, final boolean createSession) {
        if (createSession) {
            generateIdIfNecessary();
        }
        return Optional.ofNullable(id);
    }

    protected void generateIdIfNecessary() {
        if (id == null) {
            id = new Date().toString();
        }
    }

    @Override
    public Optional get(final WebContext context, final String key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public void set(final WebContext context, final String key, final Object value) {
        generateIdIfNecessary();
        store.put(key, value);
    }

    @Override
    public boolean destroySession(final WebContext context) {
        store.clear();
        id = null;
        return true;
    }

    @Override
    public Optional getTrackableSession(final WebContext context) {
        return Optional.of(store);
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(final WebContext context, final Object trackableSession) {
        return Optional.of(new MockSessionStore((Map<String, Object>) trackableSession));
    }

    @Override
    public boolean renewSession(final WebContext context) {
        id = null;
        return true;
    }
}

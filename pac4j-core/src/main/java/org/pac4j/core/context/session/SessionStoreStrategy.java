package org.pac4j.core.context.session;

import org.pac4j.core.util.CommonHelper;

/**
 * Defines the strategy related to the session store.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class SessionStoreStrategy {

    private final boolean readFromSession;
    private final boolean saveIntoSession;

    public SessionStoreStrategy(final boolean readFromSession, final boolean saveIntoSession) {
        this.readFromSession = readFromSession;
        this.saveIntoSession = saveIntoSession;
    }

    public boolean mustReadFromSession() {
        return this.readFromSession;
    }

    public boolean mustSaveIntoSession() {
        return this.saveIntoSession;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "readFromSession", this.readFromSession,
            "saveIntoSession", this.saveIntoSession);
    }
}

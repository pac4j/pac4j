package org.pac4j.core.context.session;

import org.pac4j.core.util.CommonHelper;

/**
 * Defines the strategy related to load/save the profile from/into the session store.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class ProfileStorageStrategy {

    public static final ProfileStorageStrategy NEVER_USE_THE_SESSION = new ProfileStorageStrategy(false, false);
    public static final ProfileStorageStrategy READ_FROM_THE_SESSION = new ProfileStorageStrategy(true, false);
    public static final ProfileStorageStrategy SAVE_INTO_THE_SESSION = new ProfileStorageStrategy(false, true);
    public static final ProfileStorageStrategy ALWAYS_USE_THE_SESSION = new ProfileStorageStrategy(true, true);

    private final boolean readFromSession;
    private final boolean saveIntoSession;

    protected ProfileStorageStrategy(final boolean readFromSession, final boolean saveIntoSession) {
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

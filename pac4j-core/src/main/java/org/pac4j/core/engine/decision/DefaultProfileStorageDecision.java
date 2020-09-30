package org.pac4j.core.engine.decision;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * Default decision class where the indirect clients are handled separately from the direct clients.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class DefaultProfileStorageDecision implements ProfileStorageDecision {

    /**
     * Always load the profiles from the web session.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return whether the profiles must be loaded from the web session
     */
    @Override
    public boolean mustLoadProfilesFromSession(final WebContext context, final List<Client> currentClients) {
        return true;
    }

    /**
     * Never save the profile in session after a direct client authentication.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @param directClient the direct clients
     * @param profile the retrieved profile after login
     * @return <code>false</code>
     */
    @Override
    public boolean mustSaveProfileInSession(final WebContext context, final List<Client> currentClients,
                                            final DirectClient directClient, final UserProfile profile) {
        return false;
    }
}

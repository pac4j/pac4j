package org.pac4j.core.engine.decision;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

import static org.pac4j.core.util.CommonHelper.isEmpty;

/**
 * Default decision class where the indirect clients are handled separately from the direct clients.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class DefaultProfileStorageDecision<C extends WebContext> implements ProfileStorageDecision<C> {

    /**
     * Load the profiles from the web session if no clients are defined or if the first client is an indirect one
     * or if the first client is the anonymous one.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return whether the profiles must be loaded from the web session
     */
    @Override
    public boolean mustLoadProfilesFromSession(final C context, final List<Client> currentClients) {
        return isEmpty(currentClients) || currentClients.get(0) instanceof IndirectClient ||
            currentClients.get(0) instanceof AnonymousClient;
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
    public boolean mustSaveProfileInSession(final C context, final List<Client> currentClients,
                                            final DirectClient directClient, final UserProfile profile) {
        return false;
    }
}

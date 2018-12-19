package org.pac4j.core.engine.decision;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * Defines the decisions related to load/save the profile(s) from/into the session store.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public interface ProfileStorageDecision<C extends WebContext> {

    /**
     * Whether we must load the profiles from the web session.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return whether the profiles must be loaded from the web session
     */
    boolean mustLoadProfilesFromSession(C context, List<Client> currentClients);

    /**
     * Whether we must save the profile in session after the authentication of direct clients.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @param directClient the direct clients
     * @param profile the retrieved profile after login
     * @return whether we must save the profile in session
     */
    boolean mustSaveProfileInSession(C context, List<Client> currentClients, DirectClient directClient, UserProfile profile);
}

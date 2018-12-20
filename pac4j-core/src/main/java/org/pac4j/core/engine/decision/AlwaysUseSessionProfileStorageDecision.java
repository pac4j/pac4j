package org.pac4j.core.engine.decision;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * A decision class where the session is always used, generally when indirect and direct clients are mixed in the same web application.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class AlwaysUseSessionProfileStorageDecision<C extends WebContext> implements ProfileStorageDecision<C> {

    @Override
    public boolean mustLoadProfilesFromSession(final C context, final List<Client> currentClients) {
        return true;
    }

    @Override
    public boolean mustSaveProfileInSession(final C context, final List<Client> currentClients,
                                            final DirectClient directClient, final UserProfile profile) {
        return true;
    }
}

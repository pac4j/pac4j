package org.pac4j.core.engine.strategy;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

/**
 * A strategy where the session is always used, generally when indirect and direct clients are mixed in the same web application.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class AlwaysUseSessionProfileStorageStrategy<C extends WebContext> implements ProfileStorageStrategy<C> {

    @Override
    public boolean mustLoadProfilesFromSession(final C context, final List<Client> currentClients) {
        return true;
    }

    @Override
    public boolean mustSaveProfileInSession(final C context, final List<Client> currentClients,
                                            final DirectClient directClient, final CommonProfile profile) {
        return true;
    }
}

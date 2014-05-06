package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;

/**
 * Fake client.
 * 
 * @author Jerome Leleu
 * @since 1.5.1
 */
public class FakeClient extends BaseClient<Credentials, CommonProfile> {

    @Override
    protected BaseClient<Credentials, CommonProfile> newClient() {
        throw new UnsupportedOperationException("Not implemneted yet");
    }

    @Override
    protected boolean isDirectRedirection() {
        throw new UnsupportedOperationException("Not implemneted yet");
    }

    @Override
    protected RedirectAction retrieveRedirectAction(WebContext context) {
        throw new UnsupportedOperationException("Not implemneted yet");
    }

    @Override
    protected Credentials retrieveCredentials(WebContext context) throws RequiresHttpAction {
        throw new UnsupportedOperationException("Not implemneted yet");
    }

    @Override
    protected CommonProfile retrieveUserProfile(Credentials credentials, WebContext context) {
        throw new UnsupportedOperationException("Not implemneted yet");
    }

    @Override
    public Protocol getProtocol() {
        throw new UnsupportedOperationException("Not implemneted yet");
    }

    @Override
    protected void internalInit() {
        throw new UnsupportedOperationException("Not implemneted yet");
    }
}

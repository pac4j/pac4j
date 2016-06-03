package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

/**
 * Mock an indirect client.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class MockIndirectClient extends IndirectClient<Credentials, CommonProfile> {

    private final RedirectAction redirectAction;

    private final ReturnCredentials returnCredentials;

    private final CommonProfile profile;

    public MockIndirectClient(final String name, final RedirectAction redirectAction, final Credentials credentials, final CommonProfile profile) {
        this(name, redirectAction, () -> credentials, profile);
    }

    public MockIndirectClient(final String name, final RedirectAction redirectAction, final ReturnCredentials returnCredentials, final CommonProfile profile) {
        setName(name);
        this.redirectAction = redirectAction;
        this.returnCredentials = returnCredentials;
        this.profile = profile;
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) throws HttpAction {
        return redirectAction;
    }

    @Override
    protected Credentials retrieveCredentials(final WebContext context) throws HttpAction {
        return returnCredentials.get();
    }

    @Override
    protected CommonProfile retrieveUserProfile(final Credentials credentials, final WebContext context) throws HttpAction {
        return profile;
    }
}

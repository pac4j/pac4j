package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;

/**
 * Mock an indirect client.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class MockIndirectClient extends IndirectClient<Credentials, CommonProfile> {

    private RedirectAction redirectAction;

    private ReturnCredentials returnCredentials;

    private CommonProfile profile;

    public MockIndirectClient(final String name) {
        setName(name);
    }

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
    protected void internalInit(final WebContext context) {
        super.internalInit(context);

        setRedirectActionBuilder(ctx -> redirectAction);
        setCredentialsExtractor(ctx -> returnCredentials.get());
        setAuthenticator((cred, ctx) -> cred.setUserProfile(profile));
        setLogoutActionBuilder(getLogoutActionBuilder());
    }
}

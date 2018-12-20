package org.pac4j.core.client;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;

/**
 * Mock an indirect client.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class MockIndirectClient extends IndirectClient<Credentials> {

    private HttpAction redirectAction;

    private ReturnCredentials returnCredentials;

    private CommonProfile profile;

    public MockIndirectClient(final String name) {
        setName(name);
    }

    public MockIndirectClient(final String name, final HttpAction redirectAction, final Credentials credentials,
                              final CommonProfile profile) {
        this(name, redirectAction, () -> credentials, profile);
    }

    public MockIndirectClient(final String name, final HttpAction redirectAction, final ReturnCredentials returnCredentials,
                              final CommonProfile profile) {
        setName(name);
        this.redirectAction = redirectAction;
        this.returnCredentials = returnCredentials;
        this.profile = profile;
    }

    @Override
    protected void clientInit() {
        defaultRedirectionActionBuilder(ctx -> redirectAction);
        defaultCredentialsExtractor(ctx -> returnCredentials.get());
        defaultAuthenticator((cred, ctx) -> cred.setUserProfile(profile));
        defaultLogoutActionBuilder(getLogoutActionBuilder());
    }
}

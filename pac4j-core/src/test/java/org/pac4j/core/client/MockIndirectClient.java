package org.pac4j.core.client;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;

import java.util.Optional;

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

    public MockIndirectClient(final String name, final RedirectAction redirectAction, final Credentials credentials,
                              final CommonProfile profile) {
        this(name, redirectAction, () -> credentials, profile);
    }

    public MockIndirectClient(final String name, final RedirectAction redirectAction, final ReturnCredentials returnCredentials,
                              final CommonProfile profile) {
        setName(name);
        this.redirectAction = redirectAction;
        this.returnCredentials = returnCredentials;
        this.profile = profile;
    }

    @Override
    protected void clientInit() {
        defaultRedirectActionBuilder(ctx -> redirectAction);
        defaultCredentialsExtractor(ctx -> Optional.ofNullable(returnCredentials.get()));
        defaultAuthenticator((cred, ctx) -> cred.setUserProfile(profile));
        defaultLogoutActionBuilder(getLogoutActionBuilder());
    }
}

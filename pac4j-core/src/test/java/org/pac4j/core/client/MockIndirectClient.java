package org.pac4j.core.client;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.Optional;

/**
 * Mock an indirect client.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class MockIndirectClient extends IndirectClient {

    private RedirectionAction redirectAction;

    private ReturnCredentials returnCredentials;

    private CommonProfile profile;

    public MockIndirectClient(final String name) {
        setName(name);
    }

    public MockIndirectClient(final String name, final RedirectionAction redirectAction, final Optional<Credentials> credentials,
                              final CommonProfile profile) {
        this(name, redirectAction, () -> credentials, profile);
    }

    public MockIndirectClient(final String name, final RedirectionAction redirectAction, final ReturnCredentials returnCredentials,
                              final CommonProfile profile) {
        setName(name);
        this.redirectAction = redirectAction;
        this.returnCredentials = returnCredentials;
        this.profile = profile;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        defaultRedirectionActionBuilder((ctx, store) -> Optional.of(redirectAction));
        defaultCredentialsExtractor((ctx, store, factory) -> returnCredentials.get());
        defaultAuthenticator((cred, ctx, store) -> {
            cred.setUserProfile(profile);
            return Optional.of(cred);
        });
        defaultLogoutActionBuilder(getLogoutActionBuilder());
    }
}

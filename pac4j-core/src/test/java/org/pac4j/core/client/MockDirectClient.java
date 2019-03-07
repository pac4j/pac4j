package org.pac4j.core.client;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;

import java.util.Optional;

/**
 * Mock a direct client.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class MockDirectClient extends DirectClient<Credentials> {

    private final ReturnCredentials returnCredentials;

    private final CommonProfile profile;

    public MockDirectClient(final String name) {
        this(name, () -> null, null);
    }

    public MockDirectClient(final String name, final Optional<Credentials> credentials, final CommonProfile profile) {
        this(name, () -> credentials, profile);
    }

    public MockDirectClient(final String name, final ReturnCredentials returnCredentials, final CommonProfile profile) {
        setName(name);
        this.returnCredentials = returnCredentials;
        this.profile = profile;
    }

    @Override
    protected void clientInit() {
        defaultCredentialsExtractor(ctx -> returnCredentials.get());
        defaultAuthenticator((cred, ctx) -> cred.setUserProfile(profile));
    }
}

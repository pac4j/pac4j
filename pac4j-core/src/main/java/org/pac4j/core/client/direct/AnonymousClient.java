package org.pac4j.core.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.AnonymousCredentials;
import org.pac4j.core.profile.AnonymousProfile;

/**
 * Anonymous client. Not to be used except for advanced use cases.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class AnonymousClient extends DirectClient<AnonymousCredentials> {

    public final static AnonymousClient INSTANCE = new AnonymousClient();

    public AnonymousClient() {
        logger.warn("AnonymousClient is an advanced feature: be careful when using it to avoid any security issue!");
    }

    @Override
    protected void clientInit() {
        defaultCredentialsExtractor(ctx -> AnonymousCredentials.INSTANCE);
        defaultAuthenticator((cred, ctx )-> {
            cred.setUserProfile(AnonymousProfile.INSTANCE);
        });
    }
}

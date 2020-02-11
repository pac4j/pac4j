package org.pac4j.core.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.AnonymousCredentials;
import org.pac4j.core.profile.AnonymousProfile;

import java.util.Optional;

/**
 * Anonymous client. Not to be used except for advanced use cases.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class AnonymousClient extends DirectClient<AnonymousCredentials> {

    public static final AnonymousClient INSTANCE = new AnonymousClient();

    private static boolean warned;

    public AnonymousClient() {
        if (!warned) {
            logger.warn("AnonymousClient is an advanced feature: be careful when using it to avoid any security issue!");
            warned = true;
        }
    }

    @Override
    protected void clientInit() {
        defaultCredentialsExtractor(ctx -> Optional.of(AnonymousCredentials.INSTANCE));
        defaultAuthenticator((cred, ctx )-> {
            cred.setUserProfile(AnonymousProfile.INSTANCE);
        });
    }
}

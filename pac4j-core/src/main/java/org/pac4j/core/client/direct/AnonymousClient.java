package org.pac4j.core.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.AnonymousCredentials;
import org.pac4j.core.profile.AnonymousProfile;

import java.util.Optional;

/**
 * Anonymous client.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class AnonymousClient extends DirectClient {

    public static final AnonymousClient INSTANCE = new AnonymousClient();

    private static boolean warned;

    public AnonymousClient() {
        if (!warned) {
            logger.warn("Be careful when using the 'AnonymousClient': an 'AnonymousProfile' is returned "
                + "and the access is granted for the request.");
            warned = true;
        }
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        setCredentialsExtractorIfUndefined(ctx -> Optional.of(AnonymousCredentials.INSTANCE));
        setAuthenticatorIfUndefined((ctx, cred) -> {
            cred.setUserProfile(AnonymousProfile.INSTANCE);
            return Optional.of(AnonymousCredentials.INSTANCE);
        });
    }
}

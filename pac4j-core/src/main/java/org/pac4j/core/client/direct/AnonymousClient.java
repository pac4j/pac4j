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
public final class AnonymousClient extends DirectClient<AnonymousCredentials> {

    public static final AnonymousClient INSTANCE = new AnonymousClient();

    private static boolean warned;

    public AnonymousClient() {
        if (!warned) {
            logger.warn("Be careful when using the 'AnonymousClient': an 'AnonymousProfile' is always returned "
                + "and the access is always granted by the \"security filter\". You may want to use an additional 'Authorizer'.");
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

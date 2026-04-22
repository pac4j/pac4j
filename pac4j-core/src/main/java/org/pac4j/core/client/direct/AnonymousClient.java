package org.pac4j.core.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.AnonymousCredentials;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.util.Announcement;

import java.util.Optional;

/**
 * Anonymous client.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public final class AnonymousClient extends DirectClient {

    private static final Announcement ANNOUNCEMENT = new Announcement("Be careful when using the 'AnonymousClient': an 'AnonymousProfile'"
        + " is returned and the access is granted for the request.").announce();

    /** Constant <code>INSTANCE</code> */
    public static final AnonymousClient INSTANCE = new AnonymousClient();

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setCredentialsExtractorIfUndefined(ctx -> Optional.of(AnonymousCredentials.INSTANCE));
        setAuthenticatorIfUndefined((ctx, cred) -> {
            cred.setUserProfile(AnonymousProfile.INSTANCE);
            return Optional.of(AnonymousCredentials.INSTANCE);
        });
    }
}

package org.pac4j.core.profile.creator;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * This profile creator retrieves the user profile attached in the {@link org.pac4j.core.credentials.Credentials}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class AuthenticatorProfileCreator implements ProfileCreator {

    /** Constant <code>INSTANCE</code> */
    public final static AuthenticatorProfileCreator INSTANCE = new AuthenticatorProfileCreator();

    /** {@inheritDoc} */
    @Override
    public Optional<UserProfile> create(final CallContext ctx, final Credentials credentials) {
        return Optional.ofNullable(credentials.getUserProfile());
    }
}

package org.pac4j.core.profile.creator;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;

/**
 * This profile creator retrieves the user profile attached in the {@link org.pac4j.core.credentials.Credentials}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class AuthenticatorProfileCreator<C extends Credentials, P extends UserProfile>
        implements ProfileCreator<C, P> {

    public final static AuthenticatorProfileCreator INSTANCE = new AuthenticatorProfileCreator<>();

    @Override
    public UserProfile create(final Credentials credentials) {
        return credentials.getUserProfile();
    }
}

package org.pac4j.core.logout;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * No {@link RedirectionAction} for logout.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class NoLogoutActionBuilder implements LogoutActionBuilder {

    public static final NoLogoutActionBuilder INSTANCE = new NoLogoutActionBuilder();

    @Override
    public Optional<RedirectionAction> getLogoutAction(final CallContext ctx, final UserProfile currentProfile, final String targetUrl) {
        return Optional.empty();
    }
}

package org.pac4j.core.logout;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * No {@link org.pac4j.core.exception.http.RedirectionAction} for logout.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class NoLogoutActionBuilder implements LogoutActionBuilder {

    /** Constant <code>INSTANCE</code> */
    public static final NoLogoutActionBuilder INSTANCE = new NoLogoutActionBuilder();

    /** {@inheritDoc} */
    @Override
    public Optional<RedirectionAction> getLogoutAction(final CallContext ctx, final UserProfile currentProfile, final String targetUrl) {
        return Optional.empty();
    }
}

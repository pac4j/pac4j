package org.pac4j.core.logout;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * The {@link RedirectionAction} for logout.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@FunctionalInterface
public interface LogoutActionBuilder {

    /**
     * Return the {@link RedirectionAction} for logout.
     *
     * @param ctx the context
     * @param currentProfile the current profile
     * @param targetUrl the target URL after logout
     * @return the redirection (optional)
     */
    Optional<RedirectionAction> getLogoutAction(CallContext ctx, UserProfile currentProfile, String targetUrl);
}

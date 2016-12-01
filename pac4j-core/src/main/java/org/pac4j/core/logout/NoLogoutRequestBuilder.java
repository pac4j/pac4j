package org.pac4j.core.logout;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * No logout request is returned.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class NoLogoutRequestBuilder<U extends CommonProfile> implements LogoutRequestBuilder<U> {

    public static final NoLogoutRequestBuilder INSTANCE = new NoLogoutRequestBuilder<>();

    @Override
    public LogoutRequest getLogoutRequest(final WebContext context, final U profile) {
        return null;
    }
}

package org.pac4j.core.engine;

import java.util.Collection;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

/**
 * Success adapter.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface SecurityGrantedAccessAdapter<R, C extends WebContext> {

    /**
     * Adapt the current successful action as the expected result.
     *
     * @param context the web context
     * @param parameters additional parameters
     * @param profiles the profiles granted, can be empty
     * @return an adapted result
     * @throws Exception any exception
     */
    R adapt(C context, Collection<UserProfile> profiles, Object... parameters) throws Exception;
}

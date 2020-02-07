package org.pac4j.core.profile.factory;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;

import java.util.function.Function;

/**
 * A {@link ProfileManager} factory based on the {@link WebContext}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public interface ProfileManagerFactory extends Function<WebContext, ProfileManager> {
}

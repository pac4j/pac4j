package org.pac4j.core.profile.factory;

import org.pac4j.core.profile.UserProfile;

import java.util.function.Function;

/**
 * A profile factory.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public interface ProfileFactory<P extends UserProfile> extends Function<Object[], P> {
}

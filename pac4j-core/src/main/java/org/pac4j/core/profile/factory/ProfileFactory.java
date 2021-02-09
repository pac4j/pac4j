package org.pac4j.core.profile.factory;

import org.pac4j.core.profile.UserProfile;

import java.util.function.Function;

/**
 * A profile factory.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
@FunctionalInterface
public interface ProfileFactory extends Function<Object[], UserProfile> {
}

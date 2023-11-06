package org.pac4j.core.authorization.generator;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.profile.UserProfile;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Grant default roles to a user profile.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultRolesAuthorizationGenerator implements AuthorizationGenerator {

    private final Collection<String> defaultRoles;

    /**
     * <p>Constructor for DefaultRolesAuthorizationGenerator.</p>
     */
    public DefaultRolesAuthorizationGenerator() {}

    /**
     * <p>Constructor for DefaultRolesAuthorizationGenerator.</p>
     *
     * @param defaultRoles a {@link Collection} object
     */
    public DefaultRolesAuthorizationGenerator(final Collection<String> defaultRoles) {
        this.defaultRoles = defaultRoles;
    }

    /**
     * <p>Constructor for DefaultRolesAuthorizationGenerator.</p>
     *
     * @param defaultRoles an array of {@link String} objects
     */
    public DefaultRolesAuthorizationGenerator(final String[] defaultRoles) {
        if (defaultRoles != null) {
            this.defaultRoles = Arrays.asList(defaultRoles);
        } else {
            this.defaultRoles = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<UserProfile> generate(final CallContext ctx, final UserProfile profile) {
        if (defaultRoles != null) {
            profile.addRoles(defaultRoles);
        }
        return Optional.of(profile);
    }

    /**
     * Setter for defaultRoles
     *
     * @param defaultRolesStr a coma-separated string of role names
     */
    public void setDefaultRoles(final String defaultRolesStr) {
        this.defaultRoles = Arrays.asList(defaultRolesStr.split(","));
    }
}

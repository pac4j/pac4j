package org.pac4j.core.profile;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Set;

/**
 * A minimal user profile: the {@link #getId()} method must be implemented,
 * absolutely required methods have a default behavior, others throw a {@link UnsupportedOperationException}.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public interface UserProfile extends Serializable {

    String getId();

    default String getUsername() {
        return null;
    }

    default Object getAttribute(String name) {
        throw new UnsupportedOperationException("Your user profile must implement the getAttribute method");
    }

    default boolean containsAttribute(String name) {
        throw new UnsupportedOperationException("Your user profile must implement the containsAttribute method");
    }

    default void addRole(String role) {
        throw new UnsupportedOperationException("Your user profile must implement the addRole method");
    }

    default void addRoles(Collection<String> roles) {
        throw new UnsupportedOperationException("Your user profile must implement the addRoles method");
    }

    default Set<String> getRoles() {
        throw new UnsupportedOperationException("Your user profile must implement the getRoles method");
    }

    default void addPermission(String permission) {
        throw new UnsupportedOperationException("Your user profile must implement the addPermission method");
    }

    default void addPermissions(Collection<String> permissions) {
        throw new UnsupportedOperationException("Your user profile must implement the addPermissions method");
    }

    default Set<String> getPermissions() {
        throw new UnsupportedOperationException("Your user profile must implement the getPermissions method");
    }

    default boolean isRemembered() {
        throw new UnsupportedOperationException("Your user profile must implement the isRemembered method");
    }

    default void setRemembered(boolean rme) {}

    default String getClientName() {
        return null;
    }

    default void setClientName(String clientName) {}

    default boolean isExpired() {
        return false;
    }

    default Principal asPrincipal() {
        return new Pac4JPrincipal(this);
    }
}

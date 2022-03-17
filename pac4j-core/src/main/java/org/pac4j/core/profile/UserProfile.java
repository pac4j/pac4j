package org.pac4j.core.profile;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The contract that all user profiles must respect.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public interface UserProfile extends Serializable {

    String getId();

    void setId(String id);

    String getTypedId();

    String getUsername();

    Object getAttribute(String name);

    Map<String, Object> getAttributes();

    boolean containsAttribute(String name);

    void addAttribute(String key, Object value);

    void removeAttribute(String key);

    void addAuthenticationAttribute(String key, Object value);

    void removeAuthenticationAttribute(String key);

    void addRole(String role);

    void addRoles(Collection<String> roles);

    Set<String> getRoles();

    void addPermission(String permission);

    void addPermissions(Collection<String> permissions);

    Set<String> getPermissions();

    boolean isRemembered();

    void setRemembered(boolean rme);

    String getClientName();

    void setClientName(String clientName);

    String getLinkedId();

    void setLinkedId(String linkedId);

    boolean isExpired();

    Principal asPrincipal();
}

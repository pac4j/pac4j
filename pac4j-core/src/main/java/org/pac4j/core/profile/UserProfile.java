package org.pac4j.core.profile;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="class")
public interface UserProfile extends Serializable {

    /**
     * <p>getId.</p>
     *
     * @return a {@link String} object
     */
    String getId();

    /**
     * <p>setId.</p>
     *
     * @param id a {@link String} object
     */
    void setId(String id);

    /**
     * <p>getTypedId.</p>
     *
     * @return a {@link String} object
     */
    String getTypedId();

    /**
     * <p>getUsername.</p>
     *
     * @return a {@link String} object
     */
    String getUsername();

    /**
     * <p>getAttribute.</p>
     *
     * @param name a {@link String} object
     * @return a {@link Object} object
     */
    Object getAttribute(String name);

    /**
     * <p>getAttributes.</p>
     *
     * @return a {@link Map} object
     */
    Map<String, Object> getAttributes();

    /**
     * <p>containsAttribute.</p>
     *
     * @param name a {@link String} object
     * @return a boolean
     */
    boolean containsAttribute(String name);

    /**
     * <p>addAttribute.</p>
     *
     * @param key a {@link String} object
     * @param value a {@link Object} object
     */
    void addAttribute(String key, Object value);

    /**
     * <p>removeAttribute.</p>
     *
     * @param key a {@link String} object
     */
    void removeAttribute(String key);

    /**
     * <p>addAuthenticationAttribute.</p>
     *
     * @param key a {@link String} object
     * @param value a {@link Object} object
     */
    void addAuthenticationAttribute(String key, Object value);

    /**
     * <p>removeAuthenticationAttribute.</p>
     *
     * @param key a {@link String} object
     */
    void removeAuthenticationAttribute(String key);

    /**
     * <p>addRole.</p>
     *
     * @param role a {@link String} object
     */
    void addRole(String role);

    /**
     * <p>addRoles.</p>
     *
     * @param roles a {@link Collection} object
     */
    void addRoles(Collection<String> roles);

    /**
     * <p>getRoles.</p>
     *
     * @return a {@link Set} object
     */
    Set<String> getRoles();

    /**
     * <p>isRemembered.</p>
     *
     * @return a boolean
     */
    boolean isRemembered();

    /**
     * <p>setRemembered.</p>
     *
     * @param rme a boolean
     */
    void setRemembered(boolean rme);

    /**
     * <p>getClientName.</p>
     *
     * @return a {@link String} object
     */
    String getClientName();

    /**
     * <p>setClientName.</p>
     *
     * @param clientName a {@link String} object
     */
    void setClientName(String clientName);

    /**
     * <p>getLinkedId.</p>
     *
     * @return a {@link String} object
     */
    String getLinkedId();

    /**
     * <p>setLinkedId.</p>
     *
     * @param linkedId a {@link String} object
     */
    void setLinkedId(String linkedId);

    /**
     * <p>isExpired.</p>
     *
     * @return a boolean
     */
    boolean isExpired();

    /**
     * <p>asPrincipal.</p>
     *
     * @return a {@link Principal} object
     */
    Principal asPrincipal();
}

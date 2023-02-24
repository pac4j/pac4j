package org.pac4j.config.ldaptive;

/**
 * Copy/pasted from CAS server v5.0.4.
 * Removed: passwordPolicy, principalTransformation, passwordEncoder, principalAttributeId, principalAttributeList,
 * allowMultiplePrincipalAttributeValues, allowMultiplePrincipalAttributeValues, credentialCriteria
 *
 * @author bidou
 */
public class LdapAuthenticationProperties extends AbstractLdapProperties {

    /**
     * The enum Authentication types.
     */
    public enum AuthenticationTypes {
        /**
         * Active Directory.
         */
        AD,
        /**
         * Authenticated Search.
         */
        AUTHENTICATED,
        /**
         * Direct Bind.
         */
        DIRECT,
        /**
         * Anonymous Search.
         */
        ANONYMOUS,
        /**
         * SASL bind search.
         */
        SASL
    }

    private String dnFormat;
    private String principalAttributePassword;
    private AuthenticationTypes type;

    private boolean subtreeSearch = true;
    private String baseDn;
    private String userFilter;

    private boolean enhanceWithEntryResolver = true;

    /**
     * <p>isEnhanceWithEntryResolver.</p>
     *
     * @return a boolean
     */
    public boolean isEnhanceWithEntryResolver() {
        return enhanceWithEntryResolver;
    }

    /**
     * <p>Setter for the field <code>enhanceWithEntryResolver</code>.</p>
     *
     * @param enhanceWithEntryResolver a boolean
     */
    public void setEnhanceWithEntryResolver(final boolean enhanceWithEntryResolver) {
        this.enhanceWithEntryResolver = enhanceWithEntryResolver;
    }

    /**
     * <p>Getter for the field <code>baseDn</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getBaseDn() {
        return baseDn;
    }

    /**
     * <p>Setter for the field <code>baseDn</code>.</p>
     *
     * @param baseDn a {@link java.lang.String} object
     */
    public void setBaseDn(final String baseDn) {
        this.baseDn = baseDn;
    }

    /**
     * <p>Getter for the field <code>userFilter</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUserFilter() {
        return userFilter;
    }

    /**
     * <p>Setter for the field <code>userFilter</code>.</p>
     *
     * @param userFilter a {@link java.lang.String} object
     */
    public void setUserFilter(final String userFilter) {
        this.userFilter = userFilter;
    }

    /**
     * <p>isSubtreeSearch.</p>
     *
     * @return a boolean
     */
    public boolean isSubtreeSearch() {
        return subtreeSearch;
    }

    /**
     * <p>Setter for the field <code>subtreeSearch</code>.</p>
     *
     * @param subtreeSearch a boolean
     */
    public void setSubtreeSearch(final boolean subtreeSearch) {
        this.subtreeSearch = subtreeSearch;
    }

    /**
     * <p>Getter for the field <code>dnFormat</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDnFormat() {
        return dnFormat;
    }

    /**
     * <p>Setter for the field <code>dnFormat</code>.</p>
     *
     * @param dnFormat a {@link java.lang.String} object
     */
    public void setDnFormat(final String dnFormat) {
        this.dnFormat = dnFormat;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link org.pac4j.config.ldaptive.LdapAuthenticationProperties.AuthenticationTypes} object
     */
    public AuthenticationTypes getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link org.pac4j.config.ldaptive.LdapAuthenticationProperties.AuthenticationTypes} object
     */
    public void setType(final AuthenticationTypes type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>principalAttributePassword</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPrincipalAttributePassword() {
        return principalAttributePassword;
    }

    /**
     * <p>Setter for the field <code>principalAttributePassword</code>.</p>
     *
     * @param principalAttributePassword a {@link java.lang.String} object
     */
    public void setPrincipalAttributePassword(final String principalAttributePassword) {
        this.principalAttributePassword = principalAttributePassword;
    }
}


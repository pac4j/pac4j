package org.pac4j.config.ldaptive;

/**
 * Copy/pasted from CAS server v5.0.4.
 * Removed: passwordPolicy, principalTransformation, passwordEncoder, principalAttributeId, principalAttributeList,
 * allowMultiplePrincipalAttributeValues, allowMultiplePrincipalAttributeValues, credentialCriteria
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

    public boolean isEnhanceWithEntryResolver() {
        return enhanceWithEntryResolver;
    }

    public void setEnhanceWithEntryResolver(final boolean enhanceWithEntryResolver) {
        this.enhanceWithEntryResolver = enhanceWithEntryResolver;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(final String baseDn) {
        this.baseDn = baseDn;
    }

    public String getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(final String userFilter) {
        this.userFilter = userFilter;
    }

    public boolean isSubtreeSearch() {
        return subtreeSearch;
    }

    public void setSubtreeSearch(final boolean subtreeSearch) {
        this.subtreeSearch = subtreeSearch;
    }

    public String getDnFormat() {
        return dnFormat;
    }

    public void setDnFormat(final String dnFormat) {
        this.dnFormat = dnFormat;
    }

    public AuthenticationTypes getType() {
        return type;
    }

    public void setType(final AuthenticationTypes type) {
        this.type = type;
    }

    public String getPrincipalAttributePassword() {
        return principalAttributePassword;
    }

    public void setPrincipalAttributePassword(final String principalAttributePassword) {
        this.principalAttributePassword = principalAttributePassword;
    }
}


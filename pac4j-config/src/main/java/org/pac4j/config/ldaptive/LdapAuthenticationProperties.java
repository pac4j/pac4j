package org.pac4j.config.ldaptive;

import lombok.Getter;
import lombok.Setter;

/**
 * Copy/pasted from CAS server v5.0.4.
 * Removed: passwordPolicy, principalTransformation, passwordEncoder, principalAttributeId, principalAttributeList,
 * allowMultiplePrincipalAttributeValues, allowMultiplePrincipalAttributeValues, credentialCriteria
 *
 * @author bidou
 */
@Getter
@Setter
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
}


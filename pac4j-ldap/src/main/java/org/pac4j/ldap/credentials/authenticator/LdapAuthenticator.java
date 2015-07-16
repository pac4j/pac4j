/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.ldap.credentials.authenticator;

import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.auth.AuthenticationRequest;
import org.ldaptive.auth.AuthenticationResponse;
import org.ldaptive.auth.AuthenticationResultCode;
import org.ldaptive.auth.Authenticator;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.ldap.profile.LdapProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticator for LDAP based on the Ldaptive library and its core {@link org.ldaptive.auth.Authenticator} class.
 * It creates the user profile and stores it in the credentials for the {@link org.pac4j.http.profile.creator.AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class LdapAuthenticator implements UsernamePasswordAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Authenticator ldapAuthenticator;

    private String attributes = "";

    public LdapAuthenticator() {
    }

    public LdapAuthenticator(final Authenticator ldapAuthenticator) {
        this.ldapAuthenticator = ldapAuthenticator;
    }

    public LdapAuthenticator(final Authenticator ldapAuthenticator, final String attributes) {
        this.ldapAuthenticator = ldapAuthenticator;
        this.attributes = attributes;
    }

    public void validate(UsernamePasswordCredentials credentials) {
        CommonHelper.assertNotNull("ldapAuthenticator", ldapAuthenticator);
        CommonHelper.assertNotNull("attributes", attributes);

        final String username = credentials.getUsername();
        final String[] ldapAttributes = attributes.split(",");
        final AuthenticationResponse response;
        try {
            logger.debug("Attempting LDAP authentication for {}", credentials);
            final AuthenticationRequest request = new AuthenticationRequest(username,
                    new org.ldaptive.Credential(credentials.getPassword()),
                    ldapAttributes);
            response = this.ldapAuthenticator.authenticate(request);
        } catch (final LdapException e) {
            throw new TechnicalException("Unexpected LDAP error", e);
        }
        logger.debug("LDAP response: {}", response);

        if (response.getResult()) {
            final LdapProfile profile = createProfile(username, ldapAttributes, response.getLdapEntry());
            credentials.setUserProfile(profile);
            return;
        }

        if (AuthenticationResultCode.DN_RESOLUTION_FAILURE == response.getAuthenticationResultCode()) {
            throw new AccountNotFoundException(username + " not found.");
        }
        throw new BadCredentialsException("Invalid credentials for: " + username);
    }

    protected LdapProfile createProfile(final String username, final String[] ldapAttributes, final LdapEntry entry) {
        final LdapProfile profile = new LdapProfile();
        profile.setId(username);
        for (String ldapAttribute: ldapAttributes) {
            final LdapAttribute entryAttribute = entry.getAttribute(ldapAttribute);
            if (entryAttribute != null) {
                logger.debug("Found attribute: {}", ldapAttribute);
                if (entryAttribute.size() > 1) {
                    profile.addAttribute(ldapAttribute, entryAttribute.getStringValues());
                } else {
                    profile.addAttribute(ldapAttribute, entryAttribute.getStringValue());
                }
            }
        }
        return profile;
    }

    public Authenticator getLdapAuthenticator() {
        return ldapAuthenticator;
    }

    public void setLdapAuthenticator(Authenticator ldapAuthenticator) {
        this.ldapAuthenticator = ldapAuthenticator;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}

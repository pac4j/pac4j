package org.pac4j.ldap.profile.service;

import org.ldaptive.Connection;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.auth.AuthenticationRequest;
import org.ldaptive.auth.AuthenticationResponse;
import org.ldaptive.auth.AuthenticationResultCode;
import org.ldaptive.auth.Authenticator;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.ldap.profile.LdapProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The LDAP profile service (which supersedes the LDAP authenticator).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class LdapProfileService extends AbstractProfileService<LdapProfile> {

    private static final String UID = "uid";

    private Authenticator ldapAuthenticator;

    private Connection connection;

    private String usersDn;

    private String uidAttribute = UID;

    public LdapProfileService() {}

    public LdapProfileService(final Authenticator ldapAuthenticator) {
        this.ldapAuthenticator = ldapAuthenticator;
    }

    @Deprecated
    public LdapProfileService(final Authenticator ldapAuthenticator, final String attributes) {
        this.ldapAuthenticator = ldapAuthenticator;
        setAttributes(attributes);
    }

    public LdapProfileService(final Connection connection, final Authenticator ldapAuthenticator, final String usersDn, final String uidAttribute) {
        this.connection = connection;
        this.ldapAuthenticator = ldapAuthenticator;
        this.usersDn = usersDn;
        this.uidAttribute = uidAttribute;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("ldapAuthenticator", ldapAuthenticator);
        //CommonHelper.assertNotNull("connection", connection);
        CommonHelper.assertNull("passwordEncoder", getPasswordEncoder());
        //CommonHelper.assertNotBlank("usersDn", usersDn);
        //CommonHelper.assertNotBlank("uidAttribute", uidAttribute);
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new LdapProfile()));

        super.internalInit(context);
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
        // TODO
    }

    @Override
    protected void update(final Map<String, Object> attributes) {
        // TODO
    }

    @Override
    protected void deleteById(final String id) {
        // TODO
    }

    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        // TODO
        return null;
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        init(context);

        final String username = credentials.getUsername();
        CommonHelper.assertNotBlank(Pac4jConstants.USERNAME, username);
        final AuthenticationResponse response;
        try {
            logger.debug("Attempting LDAP authentication for: {}", credentials);
            final List<String> attributesToRead = defineAttributesToRead();
            final AuthenticationRequest request = new AuthenticationRequest(username, new org.ldaptive.Credential(credentials.getPassword()),
                    attributesToRead.toArray(new String[attributesToRead.size()]));
            response = this.ldapAuthenticator.authenticate(request);
        } catch (final LdapException e) {
            throw new TechnicalException("Unexpected LDAP error", e);
        }
        logger.debug("LDAP response: {}", response);

        if (response.getResult()) {
            final Map<String, Object> attributes = new HashMap<>();
            final LdapEntry entry = response.getLdapEntry();
            for (final LdapAttribute attribute : entry.getAttributes()) {
                final String name = attribute.getName();
                if (attribute.size() > 1) {
                    attributes.put(name, attribute.getStringValues());
                } else {
                    attributes.put(name, attribute.getStringValue());
                }
            }
            final List<Map<String, Object>> listAttributes = new ArrayList<>();
            listAttributes.add(attributes);
            final LdapProfile profile = convertAttributesToProfile(listAttributes);
            credentials.setUserProfile(profile);
            return;
        }

        if (AuthenticationResultCode.DN_RESOLUTION_FAILURE == response.getAuthenticationResultCode()) {
            throw new AccountNotFoundException(username + " not found");
        }
        throw new BadCredentialsException("Invalid credentials for: " + username);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(final Connection connection) {
        this.connection = connection;
    }

    public String getUsersDn() {
        return usersDn;
    }

    public void setUsersDn(final String usersDn) {
        this.usersDn = usersDn;
    }

    public Authenticator getLdapAuthenticator() {
        return ldapAuthenticator;
    }

    public void setLdapAuthenticator(final Authenticator ldapAuthenticator) {
        this.ldapAuthenticator = ldapAuthenticator;
    }

    public String getUidAttribute() {
        return uidAttribute;
    }

    public void setUidAttribute(final String uidAttribute) {
        this.uidAttribute = uidAttribute;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "connection", connection, "ldapAuthenticator", ldapAuthenticator, "usersDn", usersDn,
                "uidAttribute", uidAttribute, "attributes", getAttributes(), "profileDefinition", getProfileDefinition());
    }
}

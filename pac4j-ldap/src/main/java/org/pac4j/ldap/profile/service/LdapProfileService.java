package org.pac4j.ldap.profile.service;

import org.ldaptive.*;
import org.ldaptive.auth.*;
import org.ldaptive.handler.ResultPredicate;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.serializer.JsonSerializer;
import org.pac4j.ldap.profile.LdapProfile;

import java.util.*;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * The LDAP profile service (which supersedes the LDAP authenticator).
 *
 * Notice that binary attributes are not supported.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class LdapProfileService extends AbstractProfileService<LdapProfile> {

    private Authenticator ldapAuthenticator;

    private ConnectionFactory connectionFactory;

    private String usersDn;

    public LdapProfileService() {}

    public LdapProfileService(final Authenticator ldapAuthenticator) {
        this.ldapAuthenticator = ldapAuthenticator;
    }

    public LdapProfileService(final Authenticator ldapAuthenticator, final String attributes) {
        this.ldapAuthenticator = ldapAuthenticator;
        setAttributes(attributes);
    }

    public LdapProfileService(final ConnectionFactory connectionFactory, final Authenticator ldapAuthenticator, final String usersDn) {
        this.connectionFactory = connectionFactory;
        this.ldapAuthenticator = ldapAuthenticator;
        this.usersDn = usersDn;
    }

    public LdapProfileService(final ConnectionFactory connectionFactory, final Authenticator ldapAuthenticator, final String attributes,
        final String usersDn) {
        this.connectionFactory = connectionFactory;
        this.ldapAuthenticator = ldapAuthenticator;
        setAttributes(attributes);
        this.usersDn = usersDn;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("ldapAuthenticator", ldapAuthenticator);
        assertNotNull("connectionFactory", connectionFactory);
        assertNull("passwordEncoder", getPasswordEncoder());
        assertNotBlank("usersDn", usersDn);

        defaultProfileDefinition(new CommonProfileDefinition(x -> new LdapProfile()));
        setSerializer(new JsonSerializer(LdapProfile.class));

        super.internalInit(forceReinit);
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
        attributes.put("objectClass", "person");
        final var ldapEntry = LdapEntry.builder()
            .dn(getEntryId(attributes))
            .attributes(getLdapAttributes(attributes))
            .build();

        try {
            final var add = new AddOperation(connectionFactory);
            add.setThrowCondition(ResultPredicate.NOT_SUCCESS);
            add.execute(new AddRequest(ldapEntry.getDn(), ldapEntry.getAttributes()));
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        }
    }

    protected String getEntryId(final Map<String, Object> attributes) {
        return getIdAttribute() + "=" + attributes.get(getIdAttribute()) + "," + usersDn;
    }

    protected List<LdapAttribute> getLdapAttributes(final Map<String, Object> attributes) {
        final List<LdapAttribute> ldapAttributes = new ArrayList<>();
        for (final var entry : attributes.entrySet()) {
            final var value = entry.getValue();
            if (value != null) {
                final var key = entry.getKey();
                final LdapAttribute ldapAttribute;
                if (value instanceof String) {
                    ldapAttribute = new LdapAttribute(key, (String) value);
                } else if (value instanceof List) {
                    final var list = (List<String>) value;
                    ldapAttribute = new LdapAttribute(key, list.toArray(new String[list.size()]));
                } else {
                    ldapAttribute = new LdapAttribute(key, value.toString());
                }
                ldapAttributes.add(ldapAttribute);
            }
        }
        return ldapAttributes;
    }

    @Override
    protected void update(final Map<String, Object> attributes) {
        try {
            final var modify = new ModifyOperation(connectionFactory);
            modify.setThrowCondition(ResultPredicate.NOT_SUCCESS);
            final List<AttributeModification> modifications = new ArrayList<>();
            for (final var attribute : getLdapAttributes(attributes)) {
                modifications.add(new AttributeModification(AttributeModification.Type.REPLACE, attribute));
            }
            final var modifyRequest = new ModifyRequest(
                getEntryId(attributes), modifications.toArray(new AttributeModification[modifications.size()]));
            modify.execute(modifyRequest);
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    protected void deleteById(final String id) {
        try {
            final var delete = new DeleteOperation(connectionFactory);
            delete.setThrowCondition(ResultPredicate.NOT_SUCCESS);
            delete.execute(new DeleteRequest(getIdAttribute() + "=" + id + "," + usersDn));
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        final List<Map<String, Object>> listAttributes = new ArrayList<>();
        try {
            final var search = new SearchOperation(connectionFactory);
            final var result = search.execute(new SearchRequest(usersDn,key + "=" + value,
                names.toArray(new String[names.size()])));
            for (final var entry : result.getEntries()) {
                listAttributes.add(getAttributesFromEntry(entry));
            }
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        }
        return listAttributes;
    }

    protected Map<String, Object> getAttributesFromEntry(final LdapEntry entry) {
        final Map<String, Object> attributes = new HashMap<>();
        for (final var attribute : entry.getAttributes()) {
            final var name = attribute.getName();
            if (attribute.size() > 1) {
                attributes.put(name, attribute.getStringValues());
            } else {
                attributes.put(name, attribute.getStringValue());
            }
        }
        return attributes;
    }

    @Override
    public void validate(final Credentials cred, final WebContext context, final SessionStore sessionStore) {
        init();

        final var credentials = (UsernamePasswordCredentials) cred;
        final var username = credentials.getUsername();
        assertNotBlank(Pac4jConstants.USERNAME, username);
        final AuthenticationResponse response;
        try {
            logger.debug("Attempting LDAP authentication for: {}", credentials);
            final var attributesToRead = defineAttributesToRead();
            final var request = new AuthenticationRequest(username, new Credential(credentials.getPassword()),
                    attributesToRead.toArray(new String[attributesToRead.size()]));
            response = this.ldapAuthenticator.authenticate(request);
        } catch (final LdapException e) {
            throw new TechnicalException("Unexpected LDAP error", e);
        }
        logger.debug("LDAP response: {}", response);

        if (response.isSuccess()) {
            final var entry = response.getLdapEntry();
            final List<Map<String, Object>> listAttributes = new ArrayList<>();
            listAttributes.add(getAttributesFromEntry(entry));
            final var profile = convertAttributesToProfile(listAttributes, username);
            credentials.setUserProfile(profile);
            return;
        }

        if (AuthenticationResultCode.DN_RESOLUTION_FAILURE == response.getAuthenticationResultCode()) {
            throw new AccountNotFoundException(username + " not found");
        }
        throw new BadCredentialsException("Invalid credentials for: " + username);
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
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

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "connectionFactory", connectionFactory, "ldapAuthenticator", ldapAuthenticator,
                "usersDn", usersDn, "idAttribute", getIdAttribute(), "attributes", getAttributes(),
                "profileDefinition", getProfileDefinition());
    }
}

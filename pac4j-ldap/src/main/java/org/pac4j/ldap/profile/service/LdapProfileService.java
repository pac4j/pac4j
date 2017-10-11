package org.pac4j.ldap.profile.service;

import org.ldaptive.*;
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

import java.util.*;

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
    protected void internalInit() {
        CommonHelper.assertNotNull("ldapAuthenticator", ldapAuthenticator);
        CommonHelper.assertNotNull("connectionFactory", connectionFactory);
        CommonHelper.assertNull("passwordEncoder", getPasswordEncoder());
        CommonHelper.assertNotBlank("usersDn", usersDn);
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new LdapProfile()));

        super.internalInit();
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
        attributes.put("objectClass", "person");
        final LdapEntry ldapEntry = new LdapEntry(getEntryId(attributes));
        ldapEntry.addAttributes(getLdapAttributes(attributes));

        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            connection.open();
            final AddOperation add = new AddOperation(connection);
            add.execute(new AddRequest(ldapEntry.getDn(), ldapEntry.getAttributes()));
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    protected String getEntryId(final Map<String, Object> attributes) {
        return getIdAttribute() + "=" + attributes.get(getIdAttribute()) + "," + usersDn;
    }

    protected List<LdapAttribute> getLdapAttributes(final Map<String, Object> attributes) {
        final List<LdapAttribute> ldapAttributes = new ArrayList<>();
        for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
            final Object value = entry.getValue();
            if (value != null) {
                final String key = entry.getKey();
                final LdapAttribute ldapAttribute;
                if (value instanceof String) {
                    ldapAttribute = new LdapAttribute(key, (String) value);
                } else if (value instanceof List) {
                    final List<String> list = (List<String>) value;
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
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            connection.open();
            final ModifyOperation modify = new ModifyOperation(connection);
            final ModifyRequest modifyRequest = new ModifyRequest(getEntryId(attributes));
            final List<AttributeModification> modifications = new ArrayList<>();
            for (final LdapAttribute attribute : getLdapAttributes(attributes)) {
                modifications.add(new AttributeModification(AttributeModificationType.REPLACE, attribute));
            }
            modifyRequest.setAttributeModifications(modifications.toArray(new AttributeModification[modifications.size()]));
            modify.execute(modifyRequest);
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    protected void deleteById(final String id) {
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            connection.open();
            final DeleteOperation delete = new DeleteOperation(connection);
            delete.execute(new DeleteRequest(getIdAttribute() + "=" + id + "," + usersDn));
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        final List<Map<String, Object>> listAttributes = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            connection.open();
            final SearchOperation search = new SearchOperation(connection);
            final SearchResult result = search.execute(new SearchRequest(usersDn,key + "=" + value,
                names.toArray(new String[names.size()]))).getResult();
            for (final LdapEntry entry : result.getEntries()) {
                listAttributes.add(getAttributesFromEntry(entry));
            }
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return listAttributes;
    }

    protected Map<String, Object> getAttributesFromEntry(final LdapEntry entry) {
        final Map<String, Object> attributes = new HashMap<>();
        for (final LdapAttribute attribute : entry.getAttributes()) {
            final String name = attribute.getName();
            if (attribute.size() > 1) {
                attributes.put(name, attribute.getStringValues());
            } else {
                attributes.put(name, attribute.getStringValue());
            }
        }
        return attributes;
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) {
        init();

        final String username = credentials.getUsername();
        CommonHelper.assertNotBlank(Pac4jConstants.USERNAME, username);
        final AuthenticationResponse response;
        try {
            logger.debug("Attempting LDAP authentication for: {}", credentials);
            final List<String> attributesToRead = defineAttributesToRead();
            final AuthenticationRequest request = new AuthenticationRequest(username, new Credential(credentials.getPassword()),
                    attributesToRead.toArray(new String[attributesToRead.size()]));
            response = this.ldapAuthenticator.authenticate(request);
        } catch (final LdapException e) {
            throw new TechnicalException("Unexpected LDAP error", e);
        }
        logger.debug("LDAP response: {}", response);

        if (response.getResult()) {
            final LdapEntry entry = response.getLdapEntry();
            final List<Map<String, Object>> listAttributes = new ArrayList<>();
            listAttributes.add(getAttributesFromEntry(entry));
            final LdapProfile profile = convertAttributesToProfile(listAttributes, username);
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
        return CommonHelper.toNiceString(this.getClass(), "connectionFactory", connectionFactory, "ldapAuthenticator", ldapAuthenticator,
                "usersDn", usersDn, "idAttribute", getIdAttribute(), "attributes", getAttributes(),
                "profileDefinition", getProfileDefinition());
    }
}

package org.pac4j.ldap.profile.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.ldaptive.*;
import org.ldaptive.auth.AuthenticationRequest;
import org.ldaptive.auth.AuthenticationResponse;
import org.ldaptive.auth.AuthenticationResultCode;
import org.ldaptive.auth.Authenticator;
import org.ldaptive.handler.ResultPredicate;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.Pac4jConstants;
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
@Getter
@Setter
@ToString(callSuper = true)
public class LdapProfileService extends AbstractProfileService<LdapProfile> {

    private Authenticator ldapAuthenticator;

    private ConnectionFactory connectionFactory;

    private String usersDn;

    /**
     * <p>Constructor for LdapProfileService.</p>
     */
    public LdapProfileService() {}

    /**
     * <p>Constructor for LdapProfileService.</p>
     *
     * @param ldapAuthenticator a {@link org.ldaptive.auth.Authenticator} object
     */
    public LdapProfileService(final Authenticator ldapAuthenticator) {
        this.ldapAuthenticator = ldapAuthenticator;
    }

    /**
     * <p>Constructor for LdapProfileService.</p>
     *
     * @param ldapAuthenticator a {@link org.ldaptive.auth.Authenticator} object
     * @param attributes a {@link java.lang.String} object
     */
    public LdapProfileService(final Authenticator ldapAuthenticator, final String attributes) {
        this.ldapAuthenticator = ldapAuthenticator;
        setAttributes(attributes);
    }

    /**
     * <p>Constructor for LdapProfileService.</p>
     *
     * @param connectionFactory a {@link org.ldaptive.ConnectionFactory} object
     * @param ldapAuthenticator a {@link org.ldaptive.auth.Authenticator} object
     * @param usersDn a {@link java.lang.String} object
     */
    public LdapProfileService(final ConnectionFactory connectionFactory, final Authenticator ldapAuthenticator, final String usersDn) {
        this.connectionFactory = connectionFactory;
        this.ldapAuthenticator = ldapAuthenticator;
        this.usersDn = usersDn;
    }

    /**
     * <p>Constructor for LdapProfileService.</p>
     *
     * @param connectionFactory a {@link org.ldaptive.ConnectionFactory} object
     * @param ldapAuthenticator a {@link org.ldaptive.auth.Authenticator} object
     * @param attributes a {@link java.lang.String} object
     * @param usersDn a {@link java.lang.String} object
     */
    public LdapProfileService(final ConnectionFactory connectionFactory, final Authenticator ldapAuthenticator, final String attributes,
        final String usersDn) {
        this.connectionFactory = connectionFactory;
        this.ldapAuthenticator = ldapAuthenticator;
        setAttributes(attributes);
        this.usersDn = usersDn;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("ldapAuthenticator", ldapAuthenticator);
        assertNotNull("connectionFactory", connectionFactory);
        assertNull("passwordEncoder", getPasswordEncoder());
        assertNotBlank("usersDn", usersDn);

        setProfileDefinitionIfUndefined(new CommonProfileDefinition(x -> new LdapProfile()));
        setSerializer(new JsonSerializer(LdapProfile.class));

        super.internalInit(forceReinit);
    }

    /** {@inheritDoc} */
    @Override
    protected void insert(final Map<String, Object> attributes) {
        attributes.put("objectClass", "person");
        val ldapEntry = LdapEntry.builder()
            .dn(getEntryId(attributes))
            .attributes(getLdapAttributes(attributes))
            .build();

        try {
            val add = new AddOperation(connectionFactory);
            add.setThrowCondition(ResultPredicate.NOT_SUCCESS);
            add.execute(new AddRequest(ldapEntry.getDn(), ldapEntry.getAttributes()));
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * <p>getEntryId.</p>
     *
     * @param attributes a {@link java.util.Map} object
     * @return a {@link java.lang.String} object
     */
    protected String getEntryId(final Map<String, Object> attributes) {
        return getIdAttribute() + "=" + attributes.get(getIdAttribute()) + "," + usersDn;
    }

    /**
     * <p>getLdapAttributes.</p>
     *
     * @param attributes a {@link java.util.Map} object
     * @return a {@link java.util.List} object
     */
    protected List<LdapAttribute> getLdapAttributes(final Map<String, Object> attributes) {
        val ldapAttributes = new ArrayList<LdapAttribute>();
        for (val entry : attributes.entrySet()) {
            val value = entry.getValue();
            if (value != null) {
                val key = entry.getKey();
                final LdapAttribute ldapAttribute;
                if (value instanceof String) {
                    ldapAttribute = new LdapAttribute(key, (String) value);
                } else if (value instanceof List) {
                    val list = (List<String>) value;
                    ldapAttribute = new LdapAttribute(key, list.toArray(new String[list.size()]));
                } else {
                    ldapAttribute = new LdapAttribute(key, value.toString());
                }
                ldapAttributes.add(ldapAttribute);
            }
        }
        return ldapAttributes;
    }

    /** {@inheritDoc} */
    @Override
    protected void update(final Map<String, Object> attributes) {
        try {
            val modify = new ModifyOperation(connectionFactory);
            modify.setThrowCondition(ResultPredicate.NOT_SUCCESS);
            val modifications = new ArrayList<AttributeModification>();
            for (val attribute : getLdapAttributes(attributes)) {
                modifications.add(new AttributeModification(AttributeModification.Type.REPLACE, attribute));
            }
            val modifyRequest = new ModifyRequest(
                getEntryId(attributes), modifications.toArray(new AttributeModification[modifications.size()]));
            modify.execute(modifyRequest);
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void deleteById(final String id) {
        try {
            val delete = new DeleteOperation(connectionFactory);
            delete.setThrowCondition(ResultPredicate.NOT_SUCCESS);
            delete.execute(new DeleteRequest(getIdAttribute() + "=" + id + "," + usersDn));
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        val listAttributes = new ArrayList<Map<String, Object>>();
        try {
            val search = new SearchOperation(connectionFactory);
            val result = search.execute(new SearchRequest(usersDn,key + "=" + value,
                names.toArray(new String[names.size()])));
            for (val entry : result.getEntries()) {
                listAttributes.add(getAttributesFromEntry(entry));
            }
        } catch (final LdapException e) {
            throw new TechnicalException(e);
        }
        return listAttributes;
    }

    /**
     * <p>getAttributesFromEntry.</p>
     *
     * @param entry a {@link org.ldaptive.LdapEntry} object
     * @return a {@link java.util.Map} object
     */
    protected Map<String, Object> getAttributesFromEntry(final LdapEntry entry) {
        val attributes = new HashMap<String, Object>();
        for (val attribute : entry.getAttributes()) {
            val name = attribute.getName();
            if (attribute.size() > 1) {
                attributes.put(name, attribute.getStringValues());
            } else {
                attributes.put(name, attribute.getStringValue());
            }
        }
        return attributes;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        init();

        val credentials = (UsernamePasswordCredentials) cred;
        val username = credentials.getUsername();
        assertNotBlank(Pac4jConstants.USERNAME, username);
        final AuthenticationResponse response;
        try {
            logger.debug("Attempting LDAP authentication for: {}", credentials);
            val attributesToRead = defineAttributesToRead();
            val request = new AuthenticationRequest(username, new Credential(credentials.getPassword()),
                    attributesToRead.toArray(new String[attributesToRead.size()]));
            response = this.ldapAuthenticator.authenticate(request);
        } catch (final LdapException e) {
            throw new TechnicalException("Unexpected LDAP error", e);
        }
        logger.debug("LDAP response: {}", response);

        if (response.isSuccess()) {
            val entry = response.getLdapEntry();
            val listAttributes = new ArrayList<Map<String, Object>>();
            listAttributes.add(getAttributesFromEntry(entry));
            val profile = convertAttributesToProfile(listAttributes, username);
            credentials.setUserProfile(profile);
            return Optional.of(credentials);
        }

        if (AuthenticationResultCode.DN_RESOLUTION_FAILURE == response.getAuthenticationResultCode()) {
            throw new AccountNotFoundException(username + " not found");
        }
        throw new BadCredentialsException("Invalid credentials for: " + username);
    }
}

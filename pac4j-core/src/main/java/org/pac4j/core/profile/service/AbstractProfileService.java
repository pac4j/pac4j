package org.pac4j.core.profile.service;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.MultipleAccountsFoundException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;
import static org.pac4j.core.util.CommonHelper.*;
import static org.pac4j.core.util.Pac4jConstants.PASSWORD;
import static org.pac4j.core.util.Pac4jConstants.USERNAME;

/**
 * Abstract implementation of the {@link ProfileService} for the storage: LDAP, SQL and MongoDB.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractProfileService<U extends CommonProfile> extends ProfileDefinitionAware
        implements ProfileService<U>, Authenticator {

    public static final String ID = "id";

    public static final String LINKEDID = "linkedid";

    public static final String SERIALIZED_PROFILE = "serializedprofile";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Setter
    @Getter
    private String usernameAttribute = USERNAME;
    @Setter
    @Getter
    private String passwordAttribute = PASSWORD;
    @Setter
    @Getter
    private String idAttribute = ID;

    @Setter
    @Getter
    private PasswordEncoder passwordEncoder;

    @Setter
    @Getter
    private Serializer serializer;

    @Setter
    @Getter
    private String attributes;

    protected String[] attributeNames;

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("profileDefinition", getProfileDefinition());
        assertNotBlank("usernameAttribute", this.usernameAttribute);
        assertNotBlank("passwordAttribute", this.passwordAttribute);
        assertNotBlank("idAttribute", this.idAttribute);
        assertNotNull("serializer", serializer);

        if (isNotBlank(attributes)) {
            attributeNames = attributes.split(",");
            for (val attributeName : attributeNames) {
                if (getIdAttribute().equalsIgnoreCase(attributeName) || LINKEDID.equalsIgnoreCase(attributeName) ||
                        getUsernameAttribute().equalsIgnoreCase(attributeName) || getPasswordAttribute().equalsIgnoreCase(attributeName) ||
                        SERIALIZED_PROFILE.equalsIgnoreCase(attributeName)) {
                    throw new TechnicalException("The 'getIdAttribute()', linkedid, 'getUsernameAttribute()', 'getPasswordAttribute()' " +
                        "and serializedprofile attributes are not allowed");
                }
            }
        } else {
            attributeNames = new String[0];
        }
    }

    @Override
    public void create(final U profile, final String password) {
        init();

        assertNotNull("profile", profile);
        assertNotBlank(PASSWORD, password);
        assertNotBlank(ID, profile.getId());
        assertNotBlank(USERNAME, profile.getUsername());

        val attributes = convertProfileAndPasswordToAttributes(profile, password);
        insert(attributes);
    }

    @Override
    public void update(final U profile, final String password) {
        init();

        assertNotNull("profile", profile);
        assertNotBlank(ID, profile.getId());
        assertNotBlank(USERNAME, profile.getUsername());

        val attributes = convertProfileAndPasswordToAttributes(profile, password);
        update(attributes);
    }

    @Override
    public void remove(final U profile) {
        init();

        assertNotNull("profile", profile);

        removeById(profile.getId());
    }

    @Override
    public void removeById(final String id) {
        init();

        assertNotBlank(ID, id);

        deleteById(id);
    }

    /**
     * Convert a profile and a password into a map of attributes for the storage.
     *
     * @param profile the profile
     * @param password the password
     * @return the attributes
     */
    protected Map<String, Object> convertProfileAndPasswordToAttributes(final U profile, final String password) {
        val storageAttributes = new HashMap<String, Object>();
        storageAttributes.put(getIdAttribute(), profile.getId());
        storageAttributes.put(LINKEDID, profile.getLinkedId());
        storageAttributes.put(getUsernameAttribute(), profile.getUsername());
        // if a password has been provided, encode it
        if (isNotBlank(password)) {
            final String encodedPassword;
            // encode password if we have a passwordEncoder (MongoDB, SQL but not for LDAP)
            if (passwordEncoder != null) {
                encodedPassword = passwordEncoder.encode(password);
            } else {
                encodedPassword = password;
            }
            storageAttributes.put(getPasswordAttribute(), encodedPassword);
        }
        // legacy mode: save the defined attributes
        if (isLegacyMode()) {
            for (val attributeName : attributeNames) {
                storageAttributes.put(attributeName, profile.getAttribute(attributeName));
            }
        } else {
            // new behaviour (>= v2.0): save the serialized profile
            storageAttributes.put(SERIALIZED_PROFILE, serializer.serializeToString(profile));
        }
        return storageAttributes;
    }

    /**
     * Insert the attributes in the storage.
     *
     * @param attributes the attributes
     */
    protected abstract void insert(final Map<String, Object> attributes);

    /**
     * Update the attributes in the storage.
     *
     * @param attributes the attributes
     */
    protected abstract void update(final Map<String, Object> attributes);

    /**
     * Delete a profile by its identifier in the storage.
     *
     * @param id the identifier
     */
    protected abstract void deleteById(final String id);

    @Override
    public U findById(final String id) {
        init();

        assertNotBlank(getIdAttribute(), id);

        val listAttributes = read(defineAttributesToRead(), getIdAttribute(), id);
        return convertAttributesToProfile(listAttributes, null);
    }

    @Override
    public U findByLinkedId(final String linkedId) {
        init();

        assertNotBlank(LINKEDID, linkedId);

        val listAttributes = read(defineAttributesToRead(), LINKEDID, linkedId);
        return convertAttributesToProfile(listAttributes, null);
    }

    /**
     * Define the attributes to read in the storage.
     *
     * @return the attributes
     */
    protected List<String> defineAttributesToRead() {
        val names = new ArrayList<String>();
        names.add(getIdAttribute());
        names.add(LINKEDID);
        // legacy mode: 'getIdAttribute()' + linkedid + username + attributes
        if (isLegacyMode()) {
            names.add(getUsernameAttribute());
            names.addAll(Arrays.asList(attributeNames));
        } else {
            // new beahviour (>= v2.0): 'getIdAttribute()' + linkedid + serializedprofile
            names.add(SERIALIZED_PROFILE);
        }
        return names;
    }

    /**
     * Convert the list of map of attributes from the storage into a profile.
     *
     * @param listStorageAttributes the list of map of attributes
     * @param username the username used for login
     * @return the profile
     */
    protected U convertAttributesToProfile(final List<Map<String, Object>> listStorageAttributes, final String username) {
        if (listStorageAttributes == null || listStorageAttributes.size() == 0) {
            return null;
        }
        val storageAttributes = listStorageAttributes.get(0);

        val linkedId = (String) storageAttributes.get(LINKEDID);
        // legacy mode: only read the defined attributes
        if (isLegacyMode()) {
            val profile = (U) getProfileDefinition().newProfile();
            for (val attributeName : attributeNames) {
                getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, attributeName, storageAttributes.get(attributeName));
            }
            val retrievedUsername = storageAttributes.get(getUsernameAttribute());
            if (retrievedUsername != null) {
                profile.setId(ProfileHelper.sanitizeIdentifier(retrievedUsername));
            } else {
                profile.setId(username);
            }
            if (isNotBlank(linkedId)) {
                profile.setLinkedId(linkedId);
            }
            return profile;
        } else {
            // new behaviour (>= v2.0): read the serialized profile
            val serializedProfile = (String) storageAttributes.get(SERIALIZED_PROFILE);
            if (serializedProfile == null) {
                throw new TechnicalException("No serialized profile found. You should certainly define the explicit attribute names you " +
                    "want to retrieve");
            }
            val profile = (U) serializer.deserializeFromString(serializedProfile);
            if (profile == null) {
                throw new TechnicalException("No deserialized profile available. You should certainly define the explicit attribute " +
                    "names you want to retrieve");
            }
            val id = storageAttributes.get(getIdAttribute());
            if (isBlank(profile.getId()) && id != null) {
                profile.setId(ProfileHelper.sanitizeIdentifier(id));
            }
            if (isBlank(profile.getLinkedId()) && isNotBlank(linkedId)) {
                profile.setLinkedId(linkedId);
            }
            return profile;
        }
    }

    /**
     * Read the list of defined attributes in the storage for key=value query.
     *
     * @param names the attribute names to read
     * @param key the key for the query
     * @param value the value for the query
     * @return the list of map of attributes
     */
    protected abstract List<Map<String, Object>> read(final List<String> names, final String key, final String value);

    @Override
    public Optional<Credentials> validate(final Credentials cred, final WebContext context, final SessionStore sessionStore) {
        init();

        assertNotNull("credentials", cred);
        val credentials = (UsernamePasswordCredentials) cred;
        val username = credentials.getUsername();
        val password = credentials.getPassword();
        assertNotBlank(USERNAME, username);
        assertNotBlank(PASSWORD, password);

        val attributesToRead = defineAttributesToRead();
        // + password to check
        attributesToRead.add(getPasswordAttribute());

        try {
            val listAttributes = read(attributesToRead, getUsernameAttribute(), username);
            if (listAttributes == null || listAttributes.isEmpty()) {
                throw new AccountNotFoundException("No account found for: " + username);
            } else if (listAttributes.size() > 1) {
                throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
            } else {
                val retrievedPassword = (String) listAttributes.get(0).get(getPasswordAttribute());
                // check password
                if (!passwordEncoder.matches(password, retrievedPassword)) {
                    throw new BadCredentialsException("Bad credentials for: " + username);
                } else {
                    val profile = convertAttributesToProfile(listAttributes, null);
                    credentials.setUserProfile(profile);
                }
            }

        } catch (final TechnicalException e) {
            logger.debug("Authentication error", e);
            throw e;
        }

        return Optional.of(cred);
    }

    protected boolean isLegacyMode() {
        return attributes != null;
    }
}

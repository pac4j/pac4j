package org.pac4j.core.profile.service;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.pac4j.core.util.Pac4jConstants.*;
import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;
import static org.pac4j.core.util.CommonHelper.*;

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

    private String usernameAttribute = USERNAME;
    private String passwordAttribute = PASSWORD;
    private String idAttribute = ID;

    private PasswordEncoder passwordEncoder;

    private Serializer serializer;

    private String attributes;

    protected String[] attributeNames;

    @Override
    protected void internalInit() {
        assertNotNull("profileDefinition", getProfileDefinition());
        assertNotBlank("usernameAttribute", this.usernameAttribute);
        assertNotBlank("passwordAttribute", this.passwordAttribute);
        assertNotBlank("idAttribute", this.idAttribute);
        assertNotNull("serializer", serializer);

        if (isNotBlank(attributes)) {
            attributeNames = attributes.split(",");
            for (final var attributeName : attributeNames) {
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

        final var attributes = convertProfileAndPasswordToAttributes(profile, password);
        insert(attributes);
    }

    @Override
    public void update(final U profile, final String password) {
        init();

        assertNotNull("profile", profile);
        assertNotBlank(ID, profile.getId());
        assertNotBlank(USERNAME, profile.getUsername());

        final var attributes = convertProfileAndPasswordToAttributes(profile, password);
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
        final Map<String, Object> storageAttributes = new HashMap<>();
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
            for (final var attributeName : attributeNames) {
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

        final var listAttributes = read(defineAttributesToRead(), getIdAttribute(), id);
        return convertAttributesToProfile(listAttributes, null);
    }

    @Override
    public U findByLinkedId(final String linkedId) {
        init();

        assertNotBlank(LINKEDID, linkedId);

        final var listAttributes = read(defineAttributesToRead(), LINKEDID, linkedId);
        return convertAttributesToProfile(listAttributes, null);
    }

    /**
     * Define the attributes to read in the storage.
     *
     * @return the attributes
     */
    protected List<String> defineAttributesToRead() {
        final List<String> names = new ArrayList<>();
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
        final var storageAttributes = listStorageAttributes.get(0);

        final var linkedId = (String) storageAttributes.get(LINKEDID);
        // legacy mode: only read the defined attributes
        if (isLegacyMode()) {
            final var profile = (U) getProfileDefinition().newProfile();
            for (final var attributeName : attributeNames) {
                getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, attributeName, storageAttributes.get(attributeName));
            }
            final var retrievedUsername = storageAttributes.get(getUsernameAttribute());
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
            final var serializedProfile = (String) storageAttributes.get(SERIALIZED_PROFILE);
            if (serializedProfile == null) {
                throw new TechnicalException("No serialized profile found. You should certainly define the explicit attribute names you " +
                    "want to retrieve");
            }
            final var profile = (U) serializer.deserializeFromString(serializedProfile);
            if (profile == null) {
                throw new TechnicalException("No deserialized profile available. You should certainly define the explicit attribute " +
                    "names you want to retrieve");
            }
            final var id = storageAttributes.get(getIdAttribute());
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
    public void validate(final Credentials cred, final WebContext context, final SessionStore sessionStore) {
        init();

        assertNotNull("credentials", cred);
        final var credentials = (UsernamePasswordCredentials) cred;
        final var username = credentials.getUsername();
        final var password = credentials.getPassword();
        assertNotBlank(USERNAME, username);
        assertNotBlank(PASSWORD, password);

        final var attributesToRead = defineAttributesToRead();
        // + password to check
        attributesToRead.add(getPasswordAttribute());

        try {
            final var listAttributes = read(attributesToRead, getUsernameAttribute(), username);
            if (listAttributes == null || listAttributes.isEmpty()) {
                throw new AccountNotFoundException("No account found for: " + username);
            } else if (listAttributes.size() > 1) {
                throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
            } else {
                final var retrievedPassword = (String) listAttributes.get(0).get(getPasswordAttribute());
                // check password
                if (!passwordEncoder.matches(password, retrievedPassword)) {
                    throw new BadCredentialsException("Bad credentials for: " + username);
                } else {
                    final var profile = convertAttributesToProfile(listAttributes, null);
                    credentials.setUserProfile(profile);
                }
            }

        } catch (final TechnicalException e) {
            logger.debug("Authentication error", e);
            throw e;
        }
    }

    protected boolean isLegacyMode() {
        return attributes != null;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String getAttributes() {
        return attributes;
    }

    /**
     * <p>Since version 2.0 of pac4j, the profile can be saved, updated and deleted in the storage
     * by serializing the profile (in the <code>serializedprofile</code> attribute).</p>
     * <p>In addition to what existed in previous versions, the profile was built from existing attributes.
     * Setting this attribute with a list of attributes separated by commas (no aliasing) allows you
     * to use different attributes of the storage instead of the <code>serializedprofile</code> attribute.</p>
     *
     * @param attributes the attributes
     */
    public void setAttributes(final String attributes) {
        this.attributes = attributes;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(final Serializer serializer) {
        this.serializer = serializer;
    }

    public String getUsernameAttribute() {
        return usernameAttribute;
    }

    public void setUsernameAttribute(final String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    public String getPasswordAttribute() {
        return passwordAttribute;
    }

    public void setPasswordAttribute(final String passwordAttribute) {
        this.passwordAttribute = passwordAttribute;
    }

    public String getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(final String idAttribute) {
        this.idAttribute = idAttribute;
    }
}

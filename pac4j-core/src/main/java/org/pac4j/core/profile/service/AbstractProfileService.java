package org.pac4j.core.profile.service;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.JavaSerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.pac4j.core.context.Pac4jConstants.*;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Abstract implementation of the {@link ProfileService} for the storage: LDAP, SQL and MongoDB.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractProfileService<U extends CommonProfile> extends ProfileDefinitionAware<U> implements ProfileService<U>, Authenticator<UsernamePasswordCredentials> {

    public static final String ID = "id";

    public static final String LINKEDID = "linkedid";

    public static final String SERIALIZED_PROFILE = "serializedprofile";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String usernameAttribute = USERNAME;
    private String passwordAttribute = PASSWORD;
    private String idAttribute = ID;

    private PasswordEncoder passwordEncoder;

    private JavaSerializationHelper javaSerializationHelper = new JavaSerializationHelper();

    private String attributes;

    protected String[] attributeNames;

    @Override
    protected void internalInit(final WebContext context) {
        assertNotNull("profileDefinition", getProfileDefinition());
        assertNotBlank("usernameAttribute", this.usernameAttribute);
        assertNotBlank("passwordAttribute", this.passwordAttribute);
        assertNotBlank("idAttribute", this.idAttribute);

        if (isNotBlank(attributes)) {
            attributeNames = attributes.split(",");
            for (final String attributeName : attributeNames) {
                if (getIdAttribute().equalsIgnoreCase(attributeName) || LINKEDID.equalsIgnoreCase(attributeName) ||
                        getUsernameAttribute().equalsIgnoreCase(attributeName) || getPasswordAttribute().equalsIgnoreCase(attributeName) ||
                        SERIALIZED_PROFILE.equalsIgnoreCase(attributeName)) {
                    throw new TechnicalException("The 'getIdAttribute()', linkedid, 'getUsernameAttribute()', 'getPasswordAttribute()' and serializedprofile attributes are not allowed");
                }
            }
        } else {
            attributeNames = new String[0];
        }
    }

    @Override
    public void create(final U profile, final String password) {
        init(null);

        assertNotNull("profile", profile);
        assertNotBlank(PASSWORD, password);
        assertNotBlank(ID, profile.getId());
        assertNotBlank(USERNAME, profile.getUsername());

        final Map<String, Object> attributes = convertProfileAndPasswordToAttributes(profile, password);
        insert(attributes);
    }

    @Override
    public void update(final U profile, final String password) {
        init(null);

        assertNotNull("profile", profile);
        assertNotBlank(ID, profile.getId());
        assertNotBlank(USERNAME, profile.getUsername());

        final Map<String, Object> attributes = convertProfileAndPasswordToAttributes(profile, password);
        update(attributes);
    }

    @Override
    public void remove(final U profile) {
        init(null);

        assertNotNull("profile", profile);

        removeById(profile.getId());
    }

    @Override
    public void removeById(final String id) {
        init(null);

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
            for (final String attributeName : attributeNames) {
                storageAttributes.put(attributeName, profile.getAttribute(attributeName));
            }
        } else {
            // new behaviour (>= v2.0): save the serialized profile
            storageAttributes.put(SERIALIZED_PROFILE, javaSerializationHelper.serializeToBase64(profile));
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
        init(null);

        assertNotBlank(getIdAttribute(), id);

        final List<Map<String, Object>> listAttributes = read(defineAttributesToRead(), getIdAttribute(), id);
        return convertAttributesToProfile(listAttributes);
    }

    @Override
    public U findByLinkedId(final String linkedId) {
        init(null);

        assertNotBlank(LINKEDID, linkedId);

        final List<Map<String, Object>> listAttributes = read(defineAttributesToRead(), LINKEDID, linkedId);
        return convertAttributesToProfile(listAttributes);
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
     * @return the profile
     */
    protected U convertAttributesToProfile(final List<Map<String, Object>> listStorageAttributes) {
        if (listStorageAttributes == null || listStorageAttributes.size() == 0) {
            return null;
        }
        final Map<String, Object> storageAttributes = listStorageAttributes.get(0);

        final String linkedId = (String) storageAttributes.get(LINKEDID);
        // legacy mode: only read the defined attributes
        if (isLegacyMode()) {
            final U profile = getProfileDefinition().newProfile();
            for (final String attributeName : attributeNames) {
                getProfileDefinition().convertAndAdd(profile, attributeName, storageAttributes.get(attributeName));
            }
            profile.setId(storageAttributes.get(getUsernameAttribute()));
            if (isNotBlank(linkedId)) {
                profile.setLinkedId(linkedId);
            }
            return profile;
        } else {
            // new behaviour (>= v2.0): read the serialized profile
            final U profile = (U) javaSerializationHelper.unserializeFromBase64((String) storageAttributes.get(SERIALIZED_PROFILE));
            final Object id = storageAttributes.get(getIdAttribute());
            if (isBlank(profile.getId()) && id != null) {
                profile.setId(id);
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
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        init(context);

        assertNotNull("credentials", credentials);
        final String username = credentials.getUsername();
        final String password = credentials.getPassword();
        assertNotBlank(USERNAME, username);
        assertNotBlank(PASSWORD, password);

        final List<String> attributesToRead = defineAttributesToRead();
        // + password to check
        attributesToRead.add(PASSWORD);

        try {
            final List<Map<String, Object>> listAttributes = read(attributesToRead, getUsernameAttribute(), username);
            if (listAttributes == null || listAttributes.isEmpty()) {
                throw new AccountNotFoundException("No account found for: " + username);
            } else if (listAttributes.size() > 1) {
                throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
            } else {
                final String retrievedPassword = (String) listAttributes.get(0).get(getPasswordAttribute());
                // check password
                if (!passwordEncoder.matches(password, retrievedPassword)) {
                    throw new BadCredentialsException("Bad credentials for: " + username);
                } else {
                    final U profile = convertAttributesToProfile(listAttributes);
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

    @Deprecated
    public String getAttributes() {
        return attributes;
    }

    /**
     * <p>With version 2.0 of pac4j, the profile can be saved and updated (and deleted) in the storage by serialization the profile.</p>
     * <p>In previous versions, the profile was built from existing attributes. Defining this attribute with a list of attributes separated by commas (no aliasing)
     * allows you to use different attributes of the storage instead of the <code>serializedprofile</code> one.</p>
     *
     * @param attributes the atrributes
     */
    @Deprecated
    public void setAttributes(final String attributes) {
        this.attributes = attributes;
    }

    public JavaSerializationHelper getJavaSerializationHelper() {
        return javaSerializationHelper;
    }

    public void setJavaSerializationHelper(final JavaSerializationHelper javaSerializationHelper) {
        this.javaSerializationHelper = javaSerializationHelper;
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

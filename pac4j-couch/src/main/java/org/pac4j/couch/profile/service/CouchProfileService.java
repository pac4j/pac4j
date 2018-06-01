package org.pac4j.couch.profile.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.MultipleAccountsFoundException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.couch.profile.CouchProfile;
import org.pac4j.couch.profile.CouchProfileMixin;
import org.pac4j.couch.profile.repository.CouchProfileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.context.Pac4jConstants.*;
import static org.pac4j.core.util.CommonHelper.*;

/**
 * The CouchDB profile service.
 *
 * @author Elie Roux
 * @since 2.0.0
 */
public class CouchProfileService extends AbstractProfileService<CouchProfile> {

    private CouchDbConnector couchDbConnector;
    private ObjectMapper objectMapper;
    private CouchProfileRepository couchProfileRepository;

    private static final TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

    public static final String COUCH_ID = "_id";

    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes, final PasswordEncoder passwordEncoder) {
        this(couchDbConnector, attributes, passwordEncoder, false);
    }

    public CouchProfileService() {
        this((CouchDbConnector) null, null, null, false);
    }

    public CouchProfileService(boolean isExperimental) {
        this((CouchDbConnector) null, null, null, isExperimental);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector) {
        this(couchDbConnector, null, null);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, boolean isExperimental) {
        this(couchDbConnector, null, null, isExperimental);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes) {
        this(couchDbConnector, attributes, null);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes, boolean isExperimental) {
        this(couchDbConnector, attributes, null, isExperimental);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final PasswordEncoder passwordEncoder) {
        this(couchDbConnector, null, passwordEncoder);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final PasswordEncoder passwordEncoder, boolean isExperimental) {
        this(couchDbConnector, null, passwordEncoder, isExperimental);
    }

    public CouchProfileService(final CouchProfileRepository couchProfileRepository, final String attributes, final PasswordEncoder passwordEncoder) {
        setIdAttribute(COUCH_ID);
        objectMapper = new ObjectMapper();
        this.couchProfileRepository = couchProfileRepository;
        setAttributes(attributes);
        setPasswordEncoder(passwordEncoder);
    }

    public CouchProfileService(final CouchProfileRepository couchProfileRepository) {
        this(couchProfileRepository, null, null);
    }

    public CouchProfileService(final CouchProfileRepository couchProfileRepository, final String attributes) {
        this(couchProfileRepository, attributes, null);
    }

    public CouchProfileService(final CouchProfileRepository couchProfileRepository, final PasswordEncoder passwordEncoder) {
        this(couchProfileRepository, null, passwordEncoder);
    }

    public CouchProfileService(CouchDbConnector couchDbConnector, final String attributes, final PasswordEncoder passwordEncoder, boolean isExperimental) {
        setIdAttribute(COUCH_ID);
        objectMapper = new ObjectMapper();
        this.couchDbConnector = couchDbConnector;
        setAttributes(attributes);
        setPasswordEncoder(passwordEncoder);
        this.isExperimental = isExperimental;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("passwordEncoder", getPasswordEncoder());
        if (couchProfileRepository == null) {
            CommonHelper.assertTrue(couchDbConnector != null, "couchDbConnector and couchProfileRepository cannot both be null.");
        }

        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new CouchProfile()));

        if (couchProfileRepository == null && (isExperimental() || Boolean.parseBoolean(System.getProperty(EXPERIMENTAL_PROFILE_PROPERTY)))) {
            couchProfileRepository = new CouchProfileRepository(couchDbConnector);
        }

        if (couchProfileRepository != null) {
            isExperimental = true;
            getObjectMapper().addMixIn(CouchProfile.class, CouchProfileMixin.class);
            couchProfileRepository.initStandardDesignDocument();
        }

        super.internalInit();
    }

    @Override
    public void create(final CouchProfile profile, final String password) {

        if (!isExperimental()) {
            super.create(profile, password);
        } else {
            init();

            assertNotNull("profile", profile);
            assertNotBlank(PASSWORD, password);
            assertNotBlank(ID, profile.getId());
            assertNotBlank(USERNAME, profile.getUsername());

            encodeAndAddPassword(profile, password);
            insert(profile);
        }
    }

    private void encodeAndAddPassword(CouchProfile profile, String password) {
        profile.addAttribute(getPasswordAttribute(), getPasswordEncoder().encode(password));
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
        logger.debug("Insert doc: {}", attributes);
        couchDbConnector.create(attributes);
    }


    protected void insert(final CouchProfile profile) {
        logger.debug("Insert doc: {}", profile);
        couchProfileRepository.add(profile);
    }

    @Override
    public void update(final CouchProfile profile, final String password) {
        if (!isExperimental()) {
            super.update(profile, password);
        } else {
            init();

            assertNotNull("profile", profile);
            assertNotBlank(ID, profile.getId());
            assertNotBlank(USERNAME, profile.getUsername());

            encodeAndAddPassword(profile, password);
            update(profile);
        }
    }

    @Override
    protected void update(final Map<String, Object> attributes) {
        final String id = (String) attributes.get(COUCH_ID);
        try {
            final InputStream oldDocStream = couchDbConnector.getAsStream(id);
            final Map<String, Object> res = objectMapper.readValue(oldDocStream, typeRef);
            res.putAll(attributes);
            couchDbConnector.update(res);
            logger.debug("Updating id: {} with attributes: {}", id, attributes);
        } catch (DocumentNotFoundException e) {
            logger.debug("Insert doc (not found by update(): {}", attributes);
            couchDbConnector.create(attributes);
        } catch (IOException e) {
            logger.error("Unexpected IO CouchDB Exception", e);
        }
    }

    protected void update(CouchProfile profile) {
        final String id = (String) profile.getAttribute(COUCH_ID);
        try {
            final CouchProfile oldProfile = couchProfileRepository.get(id);
            profile.setRev(oldProfile.getRev());
            couchProfileRepository.update(profile);

            logger.debug("Updating id: {} with profile: {}", id, profile);
        } catch (DocumentNotFoundException e) {
            logger.debug("Insert doc (not found by update(): {}", profile);
            couchProfileRepository.add(profile);
        } catch (UpdateConflictException e) {
            logger.debug("Update failed! Update conflict in update(): {}", profile);
        }
    }

    @Override
    protected void deleteById(final String id) {
        logger.debug("Delete id: {}", id);
        try {
            if (isExperimental()) {
                final InputStream oldDocStream = couchDbConnector.getAsStream(id);
                final JsonNode oldDoc = objectMapper.readTree(oldDocStream);
                final String rev = oldDoc.get("_rev").asText();
                couchDbConnector.delete(id, rev);
            } else {
                final CouchProfile couchProfile = couchProfileRepository.get(id);
                couchProfileRepository.remove(couchProfile);
            }
        } catch (DocumentNotFoundException e) {
            logger.debug("id {} is not in the database", id);
        } catch (IOException e) {
            logger.error("Unexpected IO CouchDB Exception", e);
        } catch (UpdateConflictException e) {
            logger.error("Update conflict: {}", id);
        }
    }

    @Override
    public CouchProfile findById(final String id) {
        if (!isExperimental()) {
            return super.findById(id);
        } else {
            try {
                final CouchProfile profile = couchProfileRepository.get(id);

                final String serializedProfile = (String) profile.getAttribute(SERIALIZED_PROFILE);

                final String linkedId = profile.getLinkedId();

                if (serializedProfile == null) {
                    return profile;
                }

                final CouchProfile serProfile = (CouchProfile) getJavaSerializationHelper().unserializeFromBase64(serializedProfile);
                if (serProfile == null) {
                    logger.warn("Profile with empty serializedProfile defined: {}", profile);
                    profile.getAttributes().remove(SERIALIZED_PROFILE);
                }

                final Object sid = profile.getId();
                if (isBlank(serProfile.getId()) && sid != null) {
                    logger.warn("Profile with serializedProfile without srialized id: {}", profile);
                    serProfile.setId(profile.getId());
                }
                if (isBlank(serProfile.getLinkedId()) && isNotBlank(linkedId)) {
                    serProfile.setLinkedId(linkedId);
                }
                return serProfile;
            } catch (DocumentNotFoundException e) {
                logger.debug("id {} is not in the database", id);
                return null;
            }
        }
    }

    @Override
    public CouchProfile findByLinkedId(final String linkedId) {
        if (!isExperimental()) {
            return super.findByLinkedId(linkedId);
        } else {
            init();

            assertNotBlank(LINKEDID, linkedId);

            final List<CouchProfile> profiles = couchProfileRepository.findByLinkedId(linkedId);

            if (profiles.isEmpty()) {
                return super.findByLinkedId(linkedId);
            }

            return profiles.get(0);
        }
    }

    private Map<String, Object> populateAttributes(final Map<String, Object> rowAttributes, final List<String> names) {
        final Map<String, Object> newAttributes = new HashMap<>();
        for (final Map.Entry<String, Object> entry : rowAttributes.entrySet()) {
            final String name = entry.getKey();
            if (names == null || names.contains(name)) {
                newAttributes.put(name, entry.getValue());
            }
        }
        return newAttributes;
    }

    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        logger.debug("Reading key / value: {} / {}", key, value);
        final List<Map<String, Object>> listAttributes = new ArrayList<>();
        if (key.equals(COUCH_ID)) {
            try {
                final InputStream oldDocStream = couchDbConnector.getAsStream(value);
                final Map<String, Object> res = objectMapper.readValue(oldDocStream, typeRef);
                listAttributes.add(populateAttributes(res, names));
            } catch (DocumentNotFoundException e) {
                logger.debug("Document id {} not found", value);
            } catch (IOException e) {
                logger.error("Unexpected IO CouchDB Exception", e);
            }
        }
        else {
            // supposes a by_$key view in the design document, see documentation
            final ViewQuery query = new ViewQuery()
                    .designDocId("_design/pac4j")
                    .viewName("by_"+key)
                    .key(value);
            final ViewResult result = couchDbConnector.queryView(query);
            for (ViewResult.Row row : result.getRows()) {
                final String stringValue = row.getValue();
                Map<String, Object> res = null;
                try {
                    res = objectMapper.readValue(stringValue, typeRef);
                    listAttributes.add(populateAttributes(res, names));
                } catch (IOException e) {
                    logger.error("Unexpected IO CouchDB Exception", e);
                }
            }
        }
        logger.debug("Found: {}", listAttributes);

        return listAttributes;
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) {
        if (isExperimental()) {
            init();

            assertNotNull("credentials", credentials);
            final String username = credentials.getUsername();
            final String password = credentials.getPassword();
            assertNotBlank(USERNAME, username);
            assertNotBlank(PASSWORD, password);

            try {
                final List<CouchProfile> listProfiles = couchProfileRepository.findBy(getUsernameAttribute(), username);
                if (listProfiles == null || listProfiles.isEmpty()) {
                    throw new AccountNotFoundException("No account found for: " + username);
                } else if (listProfiles.size() > 1) {
                    throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
                } else {
                    final String retrievedPassword = (String) listProfiles.get(0).getAttribute(getPasswordAttribute());
                    // check password
                    if (!getPasswordEncoder().matches(password, retrievedPassword)) {
                        throw new BadCredentialsException("Bad credentials for: " + username);
                    } else {
                        credentials.setUserProfile(listProfiles.get(0));
                    }
                }
            } catch (final TechnicalException e) {
                logger.debug("Authentication error", e);
                throw e;
            }
        } else {
            super.validate(credentials, context);
        }
    }
    public CouchDbConnector getCouchDbConnector() {
        return couchDbConnector;
    }

    public void setCouchDbConnector(final CouchDbConnector couchDbConnector) {
        this.couchDbConnector = couchDbConnector;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "couchDbConnector", couchDbConnector, "passwordEncoder", getPasswordEncoder(),
                "attributes", getAttributes(), "profileDefinition", getProfileDefinition(),
                "idAttribute", getIdAttribute(), "usernameAttribute", getUsernameAttribute(), "passwordAttribute", getPasswordAttribute());
    }
}

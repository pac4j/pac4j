package org.pac4j.couch.profile.service;

import org.pac4j.core.util.serializer.JsonSerializer;
import org.pac4j.couch.profile.CouchProfile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

    public static final String COUCH_ID = "_id";

    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes, final PasswordEncoder passwordEncoder) {
        setIdAttribute(COUCH_ID);
        objectMapper = new ObjectMapper();
        this.couchDbConnector = couchDbConnector;
        setAttributes(attributes);
        setPasswordEncoder(passwordEncoder);
    }

    public CouchProfileService() {
        this(null, null, null);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector) {
        this(couchDbConnector, null, null);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes) {
        this(couchDbConnector, attributes, null);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final PasswordEncoder passwordEncoder) {
        this(couchDbConnector, null, passwordEncoder);
    }

    @Override
    protected void internalInit() {
        assertNotNull("passwordEncoder", getPasswordEncoder());
        assertNotNull("couchDbConnector", this.couchDbConnector);

        defaultProfileDefinition(new CommonProfileDefinition(x -> new CouchProfile()));
        setSerializer(new JsonSerializer(CouchProfile.class));

        super.internalInit();
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
        logger.debug("Insert doc: {}", attributes);
        couchDbConnector.create(attributes);
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

    @Override
    protected void deleteById(final String id) {
        logger.debug("Delete id: {}", id);
        try {
            final InputStream oldDocStream = couchDbConnector.getAsStream(id);
            final JsonNode oldDoc = objectMapper.readTree(oldDocStream);
            final String rev = oldDoc.get("_rev").asText();
            couchDbConnector.delete(id, rev);
        } catch (DocumentNotFoundException e) {
            logger.debug("id {} is not in the database", id);
        } catch (IOException e) {
            logger.error("Unexpected IO CouchDB Exception", e);
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
        return toNiceString(this.getClass(), "couchDbConnector", couchDbConnector, "passwordEncoder", getPasswordEncoder(),
                "attributes", getAttributes(), "profileDefinition", getProfileDefinition(),
                "idAttribute", getIdAttribute(), "usernameAttribute", getUsernameAttribute(), "passwordAttribute", getPasswordAttribute());
    }
}

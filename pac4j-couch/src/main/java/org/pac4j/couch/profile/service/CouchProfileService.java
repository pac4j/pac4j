package org.pac4j.couch.profile.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.serializer.JsonSerializer;
import org.pac4j.couch.profile.CouchProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * The CouchDB profile service.
 *
 * @author Elie Roux
 * @since 2.0.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CouchProfileService extends AbstractProfileService<CouchProfile> {

    private CouchDbConnector couchDbConnector;
    private ObjectMapper objectMapper;
    private static final TypeReference<HashMap<String,Object>> TYPE_REFERENCE = new TypeReference<HashMap<String,Object>>() {};

    /** Constant <code>COUCH_ID="_id"</code> */
    public static final String COUCH_ID = "_id";

    /**
     * <p>Constructor for CouchProfileService.</p>
     *
     * @param couchDbConnector a {@link org.ektorp.CouchDbConnector} object
     * @param attributes a {@link java.lang.String} object
     * @param passwordEncoder a {@link org.pac4j.core.credentials.password.PasswordEncoder} object
     */
    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes, final PasswordEncoder passwordEncoder) {
        setIdAttribute(COUCH_ID);
        objectMapper = new ObjectMapper();
        this.couchDbConnector = couchDbConnector;
        setAttributes(attributes);
        setPasswordEncoder(passwordEncoder);
    }

    /**
     * <p>Constructor for CouchProfileService.</p>
     */
    public CouchProfileService() {
        this(null, null, null);
    }

    /**
     * <p>Constructor for CouchProfileService.</p>
     *
     * @param couchDbConnector a {@link org.ektorp.CouchDbConnector} object
     */
    public CouchProfileService(final CouchDbConnector couchDbConnector) {
        this(couchDbConnector, null, null);
    }

    /**
     * <p>Constructor for CouchProfileService.</p>
     *
     * @param couchDbConnector a {@link org.ektorp.CouchDbConnector} object
     * @param attributes a {@link java.lang.String} object
     */
    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes) {
        this(couchDbConnector, attributes, null);
    }

    /**
     * <p>Constructor for CouchProfileService.</p>
     *
     * @param couchDbConnector a {@link org.ektorp.CouchDbConnector} object
     * @param passwordEncoder a {@link org.pac4j.core.credentials.password.PasswordEncoder} object
     */
    public CouchProfileService(final CouchDbConnector couchDbConnector, final PasswordEncoder passwordEncoder) {
        this(couchDbConnector, null, passwordEncoder);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("passwordEncoder", getPasswordEncoder());
        assertNotNull("couchDbConnector", this.couchDbConnector);

        setProfileDefinitionIfUndefined(new CommonProfileDefinition(x -> new CouchProfile()));
        setSerializer(new JsonSerializer(CouchProfile.class));

        super.internalInit(forceReinit);
    }

    /** {@inheritDoc} */
    @Override
    protected void insert(final Map<String, Object> attributes) {
        logger.debug("Insert doc: {}", attributes);
        couchDbConnector.create(attributes);
    }

    /** {@inheritDoc} */
    @Override
    protected void update(final Map<String, Object> attributes) {
        val id = (String) attributes.get(COUCH_ID);
        try {
            val oldDocStream = couchDbConnector.getAsStream(id);
            val res = objectMapper.readValue(oldDocStream, TYPE_REFERENCE);
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

    /** {@inheritDoc} */
    @Override
    protected void deleteById(final String id) {
        logger.debug("Delete id: {}", id);
        try {
            val oldDocStream = couchDbConnector.getAsStream(id);
            val oldDoc = objectMapper.readTree(oldDocStream);
            val rev = oldDoc.get("_rev").asText();
            couchDbConnector.delete(id, rev);
        } catch (DocumentNotFoundException e) {
            logger.debug("id {} is not in the database", id);
        } catch (IOException e) {
            logger.error("Unexpected IO CouchDB Exception", e);
        }
    }

    private Map<String, Object> populateAttributes(final Map<String, Object> rowAttributes, final List<String> names) {
        val newAttributes = new HashMap<String, Object>();
        for (val entry : rowAttributes.entrySet()) {
            val name = entry.getKey();
            if (names == null || names.contains(name)) {
                newAttributes.put(name, entry.getValue());
            }
        }
        return newAttributes;
    }

    /** {@inheritDoc} */
    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        logger.debug("Reading key / value: {} / {}", key, value);
        val listAttributes = new ArrayList<Map<String, Object>>();
        if (key.equals(COUCH_ID)) {
            try {
                val oldDocStream = couchDbConnector.getAsStream(value);
                val res = objectMapper.readValue(oldDocStream, TYPE_REFERENCE);
                listAttributes.add(populateAttributes(res, names));
            } catch (DocumentNotFoundException e) {
                logger.debug("Document id {} not found", value);
            } catch (IOException e) {
                logger.error("Unexpected IO CouchDB Exception", e);
            }
        }
        else {
            // supposes a by_$key view in the design document, see documentation
            val query = new ViewQuery()
                    .designDocId("_design/pac4j")
                    .viewName("by_"+key)
                    .key(value);
            val result = couchDbConnector.queryView(query);
            for (val row : result.getRows()) {
                val stringValue = row.getValue();
                Map<String, Object> res = null;
                try {
                    res = objectMapper.readValue(stringValue, TYPE_REFERENCE);
                    listAttributes.add(populateAttributes(res, names));
                } catch (IOException e) {
                    logger.error("Unexpected IO CouchDB Exception", e);
                }
            }
        }
        logger.debug("Found: {}", listAttributes);

        return listAttributes;
    }
}

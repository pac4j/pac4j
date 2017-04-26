package org.pac4j.couch.profile.service;

import org.pac4j.couch.profile.CouchProfile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.CommonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The MongoDB profile service.
 *
 * @author Elie Roux
 * @since 2.0.0
 */
public class CouchProfileService extends AbstractProfileService<CouchProfile> {

    private CouchDbConnector couchDbConnector;

    private String usersDatabase = "users";
    
    public static final String COUCH_ID = "_id";
    public static ObjectMapper objectMapper = new ObjectMapper();

    public CouchProfileService() {
    	setIdAttribute(COUCH_ID);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector) {
    	setIdAttribute(COUCH_ID);
        this.couchDbConnector = couchDbConnector;
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes) {
    	setIdAttribute(COUCH_ID);
        this.couchDbConnector = couchDbConnector;
        setAttributes(attributes);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final String attributes, final PasswordEncoder passwordEncoder) {
    	setIdAttribute(COUCH_ID);
        this.couchDbConnector = couchDbConnector;
        setAttributes(attributes);
        setPasswordEncoder(passwordEncoder);
    }

    public CouchProfileService(final CouchDbConnector couchDbConnector, final PasswordEncoder passwordEncoder) {
    	setIdAttribute(COUCH_ID);
        this.couchDbConnector = couchDbConnector;
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("passwordEncoder", getPasswordEncoder());
        CommonHelper.assertNotNull("couchDbConnector", this.couchDbConnector);
        CommonHelper.assertNotBlank("usersDatabase", this.usersDatabase);
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new CouchProfile()));

        super.internalInit(context);
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
    	logger.debug("Insert doc: {}", attributes);
    	couchDbConnector.create(attributes);
    }

    @Override
    protected void update(final Map<String, Object> attributes) {
    	try {
    		final String id = (String) attributes.get(COUCH_ID);
			final InputStream oldDocStream = couchDbConnector.getAsStream(id);
			final JsonNode oldDoc = objectMapper.readTree(oldDocStream);
			final String rev = oldDoc.get("_rev").asText();
			attributes.put("_rev", rev);
			couchDbConnector.update(attributes);
			logger.debug("Updating id: {} with attributes: {}", id, attributes);
		} catch (DocumentNotFoundException e) {
			couchDbConnector.create(attributes);
		} catch (IOException e) {
			logger.error("", e);
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
			logger.error("", e);
		}
    }

    /*
     * 
     * {
		  "_id": "_design/pac4j",
		  "language": "javascript",
		  "views": {
		    "by_username": {
		      "map": "function(doc) {\n    if (doc.username) emit(doc.username, doc);\n}"
		    },
		    "by_linkedid": {
		      "map": "function(doc) {\n    if (doc.linkedid) emit(doc.linkedid, doc);\n}"
		    }
		  }
		}
     * 
     */

    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        logger.debug("Reading key / value: {} / {}", key, value);
        final List<Map<String, Object>> listAttributes = new ArrayList<>();
        final TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
        if (key.equals(COUCH_ID)) {
			try {
				final InputStream oldDocStream = couchDbConnector.getAsStream(value);
				final Map<String, Object> res = objectMapper.readValue(oldDocStream, typeRef);
		        final Map<String, Object> newAttributes = new HashMap<>();
		        for (final Map.Entry<String, Object> entry : res.entrySet()) {
		            final String name = entry.getKey();
		            if (names == null || names.contains(name)) {
		                newAttributes.put(name, entry.getValue());
		            }
		        }
				listAttributes.add(newAttributes);
			} catch (DocumentNotFoundException e) {
			} catch (IOException e) {
				logger.error("", e);
			}
        }
        else {
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
			        final Map<String, Object> newAttributes = new HashMap<>();
			        for (final Map.Entry<String, Object> entry : res.entrySet()) {
			            final String name = entry.getKey();
			            if (names == null || names.contains(name)) {
			                newAttributes.put(name, entry.getValue());
			            }
			        }
					listAttributes.add(newAttributes);
				} catch (IOException e) {
					logger.error("", e);
				}
	        }
        }
        
        logger.debug("Found: {}", listAttributes);

        return listAttributes;
    }

    public String getUsersDatabase() {
        return usersDatabase;
    }

    public void setUsersDatabase(final String usersDatabase) {
        this.usersDatabase = usersDatabase;
    }

    public CouchDbConnector getCouchDbConnector() {
        return couchDbConnector;
    }

    public void setCouchDbConnector(final CouchDbConnector couchDbConnector) {
        this.couchDbConnector = couchDbConnector;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "couchDbConnector", couchDbConnector, "passwordEncoder", getPasswordEncoder(),
                "usersDatabase", usersDatabase, "attributes", getAttributes(), "profileDefinition", getProfileDefinition(),
                "idAttribute", getIdAttribute(), "usernameAttribute", getUsernameAttribute(), "passwordAttribute", getPasswordAttribute());
    }
}

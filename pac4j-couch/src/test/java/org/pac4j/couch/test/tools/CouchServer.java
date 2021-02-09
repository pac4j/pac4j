package org.pac4j.couch.test.tools;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.ektorp.CouchDbConnector;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.pac4j.core.credentials.password.ShiroPasswordEncoder;
import org.pac4j.core.util.TestsConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

import mcouch.core.InMemoryCouchDb;

import org.pac4j.core.credentials.password.PasswordEncoder;

import java.io.IOException;

/**
 * Uses a local CouchDB server.
 *
 * @author Elie Roux
 * @since 2.0.0
 */
public final class CouchServer implements TestsConstants {

    public final static PasswordEncoder PASSWORD_ENCODER = new ShiroPasswordEncoder(new DefaultPasswordService());

    public CouchDbConnector start() {
        var couchDbClient = new InMemoryCouchDb();
        couchDbClient.createDatabase("users");
        var stdHttpClient = new StdHttpClient(couchDbClient);
        var stdCouchDbInstance = new StdCouchDbInstance(stdHttpClient);
        var couchDbConnector = new StdCouchDbConnector("users", stdCouchDbInstance);

        couchDbConnector.createDatabaseIfNotExists();

        // uploading design doc:
        var designDocString = "{\"_id\":\"_design/pac4j\",\"language\":\"javascript\",\"views\":{\"by_username\":{\"map\":\""
            + "function(doc){if (doc.username) emit(doc.username, doc);}\"},\"by_linkedid\":{\"map\":\"function(doc) {"
            + "if (doc.linkedid) emit(doc.linkedid, doc);}\"}}}";
        var objectMapper = new ObjectMapper();
        try {
            couchDbConnector.create(objectMapper.readTree(designDocString));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return couchDbConnector;
    }

    public void stop() {
    }
}

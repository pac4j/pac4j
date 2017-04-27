package org.pac4j.couch.test.tools;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.pac4j.core.credentials.password.ShiroPasswordEncoder;
import org.pac4j.core.util.TestsConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.pac4j.core.credentials.password.PasswordEncoder;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Uses a local CouchDB server.
 *
 * @author Elie Roux
 * @since 2.0.0
 */
public final class CouchServer implements TestsConstants {

	public final static PasswordEncoder PASSWORD_ENCODER = new ShiroPasswordEncoder(new DefaultPasswordService());

	public CouchDbConnector start(final int port) {
		String couchUrl = "http://localhost:13598/";
		HttpClient httpClient;
		CouchDbInstance dbInstance;
		try {
			httpClient = new StdHttpClient.Builder()
					.url(couchUrl)
					.build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		dbInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector couchDbConnector = new StdCouchDbConnector("users", dbInstance);
		couchDbConnector.createDatabaseIfNotExists();

		// uploading design doc:
		String designDocString = "{\"_id\":\"_design/pac4j\",\"language\":\"javascript\",\"views\":{\"by_username\":{\"map\":\"function(doc){if (doc.username) emit(doc.username, doc);}\"},\"by_linkedid\":{\"map\":\"function(doc) {if (doc.linkedid) emit(doc.linkedid, doc);}\"}}}";
		ObjectMapper objectMapper = new ObjectMapper();
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

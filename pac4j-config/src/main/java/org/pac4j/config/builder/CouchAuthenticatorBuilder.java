package org.pac4j.config.builder;

import java.net.MalformedURLException;
import java.util.Map;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.StdHttpClient.Builder;
import org.ektorp.impl.StdCouchDbInstance;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.couch.profile.service.CouchProfileService;


/**
 * Builder for the Couch authenticator.
 *
 * @author Elie Roux
 * @since 2.1.0
 */

public class CouchAuthenticatorBuilder  extends AbstractBuilder {

    public CouchAuthenticatorBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryBuildDbAuthenticator(final Map<String, Authenticator> authenticators, final Map<String, PasswordEncoder> encoders) {
        for (int i = 0; i <= MAX_NUM_AUTHENTICATORS; i++) {
            if (containsProperty(COUCH_URL, i) && containsProperty(COUCH_DATABASENAME, i)) {
                final Builder httpClientBuilder = new StdHttpClient.Builder();
                if (containsProperty(COUCH_USERNAME, i)) {
                    httpClientBuilder.username(getProperty(COUCH_USERNAME, i));
                }
                if (containsProperty(COUCH_PASSWORD, i)) {
                    httpClientBuilder.password(getProperty(COUCH_PASSWORD, i));
                }
                try {
                    httpClientBuilder.url(getProperty(COUCH_URL, i));
                } catch (MalformedURLException e) {
                    throw new TechnicalException(e);
                }
                final String databaseName = getProperty(COUCH_DATABASENAME, i);
                final HttpClient httpClient = httpClientBuilder.build();
                final CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
                if (!dbInstance.checkIfDbExists(databaseName)) {
                    throw new TechnicalException("Database "+databaseName+" does not exist, please create it with the appropriate design doc, see http://www.pac4j.org/docs/authenticators/couchdb.html");
                }
                final CouchDbConnector db = dbInstance.createConnector(COUCH_DATABASENAME, false);
                final CouchProfileService authenticator = new CouchProfileService(db);
                if (containsProperty(COUCH_PASSWORD_ENCODER, i)) {
                    authenticator.setPasswordEncoder(encoders.get(getProperty(COUCH_PASSWORD_ENCODER, i)));
                }
                authenticators.put(concat("couch", i), authenticator);
            }
        }
    }

}

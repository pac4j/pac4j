package org.pac4j.mongo.credentials.authenticator;

import com.mongodb.MongoClient;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.mongo.profile.service.MongoProfileService;

/**
 * Use the {@link MongoProfileService} instead.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 * @deprecated
 */
@Deprecated
public class MongoAuthenticator extends MongoProfileService {

    public MongoAuthenticator() {}

    public MongoAuthenticator(final MongoClient mongoClient) {
        super(mongoClient);
    }

    @Deprecated
    public MongoAuthenticator(final MongoClient mongoClient, final String attributes) {
        super(mongoClient, attributes);
    }

    @Deprecated
    public MongoAuthenticator(final MongoClient mongoClient, final String attributes, final PasswordEncoder passwordEncoder) {
        super(mongoClient, attributes, passwordEncoder);
    }
}

package org.pac4j.mongo.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.mongo.credentials.authenticator.MongoAuthenticator;

/**
 * <p>The user profile returned from a MongoDB.</p>
 *
 * @see MongoAuthenticator
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class MongoProfile extends CommonProfile {

    private static final long serialVersionUID = 7289249610131900281L;
}

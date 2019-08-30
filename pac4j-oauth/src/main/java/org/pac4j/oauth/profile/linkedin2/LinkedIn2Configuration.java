package org.pac4j.oauth.profile.linkedin2;

import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * LinkedIn OAuth configuration.
 *
 * @author Jerome Leleu
 * @author Vassilis Virvilis 
 * @since 3.0.0
 */
public class LinkedIn2Configuration extends OAuth20Configuration {
    public final static String DEFAULT_SCOPE = "r_liteprofile r_emailaddress";

    public LinkedIn2Configuration() {
        setScope(DEFAULT_SCOPE);
    }
}

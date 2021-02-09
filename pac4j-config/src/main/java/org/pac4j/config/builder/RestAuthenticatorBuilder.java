package org.pac4j.config.builder;

import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.credentials.authenticator.RestAuthenticator;

import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for the REST authenticator.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public class RestAuthenticatorBuilder extends AbstractBuilder {

    public RestAuthenticatorBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryBuildRestAuthenticator(final Map<String, Authenticator> authenticators) {
        for (var i = 0; i <= MAX_NUM_AUTHENTICATORS; i++) {
            final var url = getProperty(REST_URL, i);
            if (isNotBlank(url)) {
                authenticators.put(concat("rest", i), new RestAuthenticator(url));
            }
        }
    }
}

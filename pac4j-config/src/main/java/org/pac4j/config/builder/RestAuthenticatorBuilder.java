package org.pac4j.config.builder;

import lombok.val;
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

    /**
     * <p>Constructor for RestAuthenticatorBuilder.</p>
     *
     * @param properties a {@link java.util.Map} object
     */
    public RestAuthenticatorBuilder(final Map<String, String> properties) {
        super(properties);
    }

    /**
     * <p>tryBuildRestAuthenticator.</p>
     *
     * @param authenticators a {@link java.util.Map} object
     */
    public void tryBuildRestAuthenticator(final Map<String, Authenticator> authenticators) {
        for (var i = 0; i <= MAX_NUM_AUTHENTICATORS; i++) {
            val url = getProperty(REST_URL, i);
            if (isNotBlank(url)) {
                authenticators.put(concat("rest", i), new RestAuthenticator(url));
            }
        }
    }
}

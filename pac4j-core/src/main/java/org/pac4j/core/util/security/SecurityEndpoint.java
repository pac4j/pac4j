package org.pac4j.core.util.security;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;

/**
 * Security endpoint.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
public interface SecurityEndpoint {

    void setClients(String clients);

    void setAuthorizers(String authorizers);

    void setMatchers(String matchers);

    void setSecurityLogic(SecurityLogic securityLogic);

    void setHttpActionAdapter(HttpActionAdapter httpActionAdapter);

    void setConfig(Config config);
}

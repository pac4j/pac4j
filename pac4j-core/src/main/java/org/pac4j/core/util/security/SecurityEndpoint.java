package org.pac4j.core.util.security;

import org.pac4j.core.config.Config;

/**
 * Security endpoint.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
public interface SecurityEndpoint {

    /**
     * <p>setClients.</p>
     *
     * @param clients a {@link String} object
     */
    void setClients(String clients);

    /**
     * <p>setAuthorizers.</p>
     *
     * @param authorizers a {@link String} object
     */
    void setAuthorizers(String authorizers);

    /**
     * <p>setMatchers.</p>
     *
     * @param matchers a {@link String} object
     */
    void setMatchers(String matchers);

    /**
     * <p>setConfig.</p>
     *
     * @param config a {@link Config} object
     */
    void setConfig(Config config);
}

package org.pac4j.core.util.security;

import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.config.Config;

/**
 * Mock of a security endpoint.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
@Getter
@Setter
public class MockSecurityEndpoint implements SecurityEndpoint{

    private String clients;

    private String authorizers;

    private String matchers;

    private Config config;
}

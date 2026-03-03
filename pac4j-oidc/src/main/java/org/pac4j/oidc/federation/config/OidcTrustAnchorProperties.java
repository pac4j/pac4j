package org.pac4j.oidc.federation.config;

import lombok.Getter;
import lombok.Setter;

/**
 * A trust anchor.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Getter
@Setter
public class OidcTrustAnchorProperties {

    private String taIssuer;

    private String taJwksUrl;
}

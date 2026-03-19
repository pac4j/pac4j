package org.pac4j.oidc.federation.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.util.CommonHelper;
import org.springframework.core.io.Resource;

/**
 * A trust anchor.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OidcTrustAnchorProperties {

    private String issuer;

    private Resource jwksResource;

    public OidcTrustAnchorProperties(final String issuer, final String jwksResourcePath) {
        setIssuer(issuer);
        setJwksPath(jwksResourcePath);
    }

    public OidcTrustAnchorProperties setJwksResource(final Resource jwksResource) {
        CommonHelper.assertNotNull("jwksResource", jwksResource);
        this.jwksResource = jwksResource;
        return this;
    }

    public OidcTrustAnchorProperties setJwksPath(final String path) {
        CommonHelper.assertNotBlank("path", path);
        return setJwksResource(SpringResourceHelper.buildResourceFromPath(path));
    }
}

package org.pac4j.oidc.federation.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.pac4j.core.config.properties.JwksProperties;
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

    private JwksProperties jwks = new JwksProperties();

    /**
     * @deprecated use {@code OidcTrustAnchorProperties(String, JwksProperties)}
     */
    @Deprecated
    public OidcTrustAnchorProperties(final String issuer, final Resource jwksResource) {
        setIssuer(issuer);
        setJwksResource(jwksResource);
    }

    /**
     * @deprecated use {@code OidcTrustAnchorProperties(String, JwksProperties)}
     */
    @Deprecated
    public OidcTrustAnchorProperties(final String issuer, final String jwksResourcePath) {
        setIssuer(issuer);
        setJwksPath(jwksResourcePath);
    }

    /**
     * @deprecated use getJwks().getResource() instead of getJwksResource()
     */
    @Deprecated
    public Resource getJwksResource() {
        return jwks.getResource();
    }

    /**
     * @deprecated use getJwks().setResource(resource) instead of setJwksResource(resource)
     */
    @Deprecated
    public OidcTrustAnchorProperties setJwksResource(final Resource jwksResource) {
        CommonHelper.assertNotNull("jwksResource", jwksResource);
        jwks.setResource(jwksResource);
        return this;
    }

    /**
     * @deprecated use getJwks().setPath(path) instead of setJwksPath(path)
     */
    @Deprecated
    public OidcTrustAnchorProperties setJwksPath(final String path) {
        CommonHelper.assertNotBlank("path", path);
        jwks.setPath(path);
        return this;
    }
}

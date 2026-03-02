package org.pac4j.oidc.federation.config;

import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.resource.SpringResourceHelper;
import org.springframework.core.io.Resource;

/**
 * A trust anchor.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Getter
@Setter
public class OidcTrustAnchorProperties {

    private String entityId;

    private Resource jwksResource;

    /**
     * <p>setJwksPath.</p>
     *
     * @param path a {@link String} object
     */
    public void setJwksPath(final String path) {
        this.jwksResource = SpringResourceHelper.buildResourceFromPath(path);
    }
}

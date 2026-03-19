package org.pac4j.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.util.CommonHelper;
import org.springframework.core.io.Resource;

/**
 * JWKS properties.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class JwksProperties {

    private Resource jwksResource;

    private String kid;

    public JwksProperties setJwksResource(final Resource jwksResource) {
        CommonHelper.assertNotNull("jwksResource", jwksResource);
        this.jwksResource = jwksResource;
        return this;
    }

    /**
     * <p>setJwksPath.</p>
     *
     * @param path a {@link String} object
     */
    public void setJwksPath(final String path) {
        this.jwksResource = SpringResourceHelper.buildResourceFromPath(path);
    }

    public boolean isDefined() {
        return jwksResource != null;
    }
}

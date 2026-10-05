package org.pac4j.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
public class JwksProperties extends ResourceProperties {

    private String kid;

    /**
     * <p>Properties without JWKS yet.</p>
     */
    public JwksProperties() {
        // the resource is set later
    }

    /**
     * <p>Properties of the JWKS at a path.</p>
     *
     * @param path the path: {@code classpath:}, {@code file:}, {@code http(s)://} or a plain file path
     */
    public JwksProperties(final String path) {
        super(path);
    }

    /** {@inheritDoc} */
    @Override
    public JwksProperties setResource(final Resource resource) {
        super.setResource(resource);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public JwksProperties setPath(final String path) {
        super.setPath(path);
        return this;
    }

    /**
     * <p>The JWKS resource.</p>
     *
     * @return the resource
     * @deprecated use {@code getResource()}
     */
    @Deprecated
    public Resource getJwksResource() {
        return getResource();
    }

    /**
     * <p>Set the JWKS resource.</p>
     *
     * @param jwksResource the resource
     * @return the properties
     * @deprecated use {@link #setResource(Resource)}
     */
    @Deprecated
    public JwksProperties setJwksResource(final Resource jwksResource) {
        CommonHelper.assertNotNull("jwksResource", jwksResource);
        return setResource(jwksResource);
    }

    /**
     * <p>Set the JWKS resource from its path.</p>
     *
     * @param path the path
     * @return the properties
     * @deprecated use {@link #setPath(String)}
     */
    @Deprecated
    public JwksProperties setJwksPath(final String path) {
        return setPath(path);
    }
}

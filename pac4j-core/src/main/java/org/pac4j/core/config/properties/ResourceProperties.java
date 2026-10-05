package org.pac4j.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.pac4j.core.resource.SpringResourceHelper;
import org.springframework.core.io.Resource;

/**
 * Resource properties: where a file is, as a path ({@code classpath:}, {@code file:}, {@code http(s)://} or a plain
 * file path) or as a resource. The more specific properties ({@link KeystoreProperties}, {@link JwksProperties})
 * extend them.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResourceProperties {

    private Resource resource;

    /**
     * <p>Properties without resource yet.</p>
     */
    public ResourceProperties() {
        // the resource is set later
    }

    /**
     * <p>Properties of the resource at a path.</p>
     *
     * @param path the path: {@code classpath:}, {@code file:}, {@code http(s)://} or a plain file path
     */
    public ResourceProperties(final String path) {
        this.resource = SpringResourceHelper.buildResourceFromPath(path);
    }

    /**
     * <p>Set the resource.</p>
     *
     * @param resource the resource
     * @return the properties
     */
    public ResourceProperties setResource(final Resource resource) {
        this.resource = resource;
        return this;
    }

    /**
     * <p>Set the resource from its path.</p>
     *
     * @param path the path: {@code classpath:}, {@code file:}, {@code http(s)://} or a plain file path
     * @return the properties
     */
    public ResourceProperties setPath(final String path) {
        this.resource = SpringResourceHelper.buildResourceFromPath(path);
        return this;
    }

    /**
     * <p>Whether the resource is defined.</p>
     *
     * @return whether the resource is defined
     */
    public boolean isDefined() {
        return resource != null;
    }
}

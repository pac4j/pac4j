package org.pac4j.core.exception.http;

/**
 * An action with a content.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@FunctionalInterface
public interface WithContentAction {

    /**
     * <p>getContent.</p>
     *
     * @return a {@link String} object
     */
    String getContent();
}

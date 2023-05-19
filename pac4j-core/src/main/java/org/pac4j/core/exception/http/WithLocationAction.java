package org.pac4j.core.exception.http;

/**
 * An action with a location.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@FunctionalInterface
public interface WithLocationAction {

    /**
     * <p>getLocation.</p>
     *
     * @return a {@link String} object
     */
    String getLocation();
}

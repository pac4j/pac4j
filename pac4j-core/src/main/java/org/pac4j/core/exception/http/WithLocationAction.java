package org.pac4j.core.exception.http;

/**
 * An action with a location.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@FunctionalInterface
public interface WithLocationAction {

    String getLocation();
}

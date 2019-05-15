package org.pac4j.core.client;

import org.pac4j.core.credentials.Credentials;

import java.util.Optional;

/**
 * Return a credentials.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface ReturnCredentials {

    Optional<Credentials> get();
}

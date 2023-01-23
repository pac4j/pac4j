package org.pac4j.core.credentials.authenticator;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.AuthenticationCredentials;
import org.pac4j.core.exception.CredentialsException;

import java.util.Optional;

/**
 * An authenticator is responsible for validating {@link AuthenticationCredentials} and should throw a {@link CredentialsException}
 * if the authentication fails.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
@FunctionalInterface
public interface Authenticator {

    Authenticator ALWAYS_VALIDATE = new Authenticator() {
        @Override
        public Optional<AuthenticationCredentials> validate(CallContext ctx, AuthenticationCredentials credentials) {
            return Optional.of(credentials);
        }
    };

    /**
     * Validate the credentials. It should throw a {@link CredentialsException} in case of failure.
     *
     * @param ctx the context
     * @param credentials the given credentials
     * @return the credentials
     */
    Optional<AuthenticationCredentials> validate(CallContext ctx, AuthenticationCredentials credentials);
}

package org.pac4j.core.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;

import java.util.Optional;

/**
 * An authenticator is responsible for validating {@link Credentials} and should throw a {@link CredentialsException}
 * if the authentication fails.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
@FunctionalInterface
public interface Authenticator {

    Authenticator ALWAYS_VALIDATE = new Authenticator() {
        @Override
        public Optional<Credentials> validate(Credentials credentials, WebContext context, SessionStore sessionStore) {
            return Optional.of(credentials);
        }
    };

    /**
     * Validate the credentials. It should throw a {@link CredentialsException} in case of failure.
     *
     * @param credentials the given credentials
     * @param context the web context
     * @param sessionStore the session store
     * @return the credentials
     */
    Optional<Credentials> validate(Credentials credentials, WebContext context, SessionStore sessionStore);
}

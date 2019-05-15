package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;

import java.util.Optional;

/**
 * A credentials extractor.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface CredentialsExtractor<C extends Credentials> {

    /**
     * Extract the {@link Credentials} from a {@link WebContext}
     * and return <code>Optional.empty()</code> if no credentials are present
     * or throw a {@link CredentialsException} if it cannot get it.
     *
     * @param context the current web context
     * @return the credentials (optional)
     */
    Optional<C> extract(WebContext context);
}

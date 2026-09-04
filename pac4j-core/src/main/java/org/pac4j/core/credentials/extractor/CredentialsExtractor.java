package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;

import java.util.Optional;

/**
 * A credentials extractor.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@FunctionalInterface
public interface CredentialsExtractor {

    /**
     * Extract the {@link Credentials} from a {@link org.pac4j.core.context.WebContext}
     * and return <code>Optional.empty()</code> if no credentials are present
     * or throw a {@link org.pac4j.core.exception.CredentialsException} if it cannot get it.
     * Note that returning an empty {@code Optional} or throwing a {@code CredentialsException} will block
     * any further authentication attempt. Any other exception will allow a retry.
     *
     * @param ctx the current context
     * @return the credentials (optional)
     */
    Optional<Credentials> extract(CallContext ctx);
}

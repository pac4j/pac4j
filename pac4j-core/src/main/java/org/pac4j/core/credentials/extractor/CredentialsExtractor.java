package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;

/**
 * An extractor gets the {@link Credentials} from a {@link WebContext} and should return <code>null</code> if no credentials are present
 * or should throw a {@link CredentialsException} if it cannot get it.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface CredentialsExtractor<C extends Credentials> {

    /**
     * Extract the right credentials. It should throw a {@link CredentialsException} in case of failure.
     *
     * @param context the current web context
     * @return the credentials
     */
    C extract(WebContext context);
}

package org.pac4j.core.http.callback;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;

/**
 * No name is added to the callback URL to be able to distinguish the client, so it doesn't match.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class NoParameterCallbackUrlResolver implements CallbackUrlResolver {

    /** {@inheritDoc} */
    @Override
    public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
        return urlResolver.compute(url, context);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(final String clientName, final WebContext context) {
        return false;
    }
}

package org.pac4j.core.http.callback;

import org.pac4j.core.context.WebContext;

/**
 * No name is added to the callback URL to be able to distinguish the client, so it doesn't match.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class NoParameterCallbackUrlResolver extends AbstractCallbackUrlResolver {

    @Override
    public String compute(final String url, final String clientName, final WebContext context) {
        return getUrlResolver().compute(url, context);
    }

    @Override
    public boolean matches(final String clientName, final WebContext context) {
        return false;
    }
}

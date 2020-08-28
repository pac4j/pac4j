package org.pac4j.core.http.callback;

import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;
import java.util.Map;

/**
 * No name is added to the callback URL to be able to distinguish the client, so it doesn't match.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class NoParameterCallbackUrlResolver extends BaseCallbackUrlResolver {
    public NoParameterCallbackUrlResolver() {
    }

    public NoParameterCallbackUrlResolver(final BaseClientConfiguration config) {
        super(config);
    }

    public NoParameterCallbackUrlResolver(final Map<String, String> customParams) {
        super(customParams);
    }

    @Override
    public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
        final String newUrl = urlResolver.compute(url, context);
        return computeUrlCustomParams(newUrl);
    }

    @Override
    public boolean matches(final String clientName, final WebContext context) {
        return false;
    }
}

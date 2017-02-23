package org.pac4j.core.http;

import org.pac4j.core.context.WebContext;

/**
 * Default URL resolver: uses the provided URL as is.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class DefaultUrlResolver implements UrlResolver {

    @Override
    public String compute(final String url, WebContext context) {
        return url;
    }
}

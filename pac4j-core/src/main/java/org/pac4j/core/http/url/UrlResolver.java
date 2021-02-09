package org.pac4j.core.http.url;

import org.pac4j.core.context.WebContext;

/**
 * How to compute an URL.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@FunctionalInterface
public interface UrlResolver {

    /**
     * Compute a new URL from the provided URL and the web context.
     *
     * @param url the provided URL
     * @param context the web context
     * @return the computed URL
     */
    String compute(String url, WebContext context);
}

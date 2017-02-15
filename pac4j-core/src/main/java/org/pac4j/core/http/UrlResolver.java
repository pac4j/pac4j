package org.pac4j.core.http;

import org.pac4j.core.context.WebContext;

/**
 * How to compute an url.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
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

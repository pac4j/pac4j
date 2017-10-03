package org.pac4j.core.http.callback;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;

/**
 * How to compute a callback URL and match a client.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public interface CallbackUrlResolver {

    /**
     * Compute a callback URL from the provided URL, the client name and the web context.
     *
     * @param url the provided URL
     * @param clientName the client name
     * @param context the web context
     * @return the computed URL
     */
    String compute(String url, String clientName, WebContext context);

    /**
     * Whether the current context matches the client name.
     *
     * @param clientName the client name
     * @param context the web context
     * @return whether the current context matches the client name
     */
    boolean matches(String clientName, WebContext context);

    /**
     * Get the underlying URL resolver.
     *
     * @return the underlying URL resolver
     */
    UrlResolver getUrlResolver();
}

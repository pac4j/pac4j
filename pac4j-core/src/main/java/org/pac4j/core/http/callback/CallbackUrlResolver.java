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
     * Compute a callback URL from the provided URL resolver, URL, client name and web context.
     *
     * @param urlResolver the provided URL resolver
     * @param url the URL
     * @param clientName the client name
     * @param context the web context
     * @return the computed URL
     */
    String compute(UrlResolver urlResolver, String url, String clientName, WebContext context);

    /**
     * Whether the current context matches the client name.
     *
     * @param clientName the client name
     * @param context the web context
     * @return whether the current context matches the client name
     */
    boolean matches(String clientName, WebContext context);
}

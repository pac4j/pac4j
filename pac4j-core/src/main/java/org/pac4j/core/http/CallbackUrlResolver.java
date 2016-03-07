package org.pac4j.core.http;

import org.pac4j.core.context.WebContext;

/**
 * How to compute the final callback url.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public interface CallbackUrlResolver {

    /**
     * Compute a new callback url from the defined callback url and the web context.
     *
     * @param callbackUrl the callback url
     * @param context the web context
     * @return the new callback url
     */
    String compute(String callbackUrl, WebContext context);
}

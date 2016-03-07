package org.pac4j.core.http;

import org.pac4j.core.context.WebContext;

/**
 * Default callback url resolver: uses the defined callback url as is.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class DefaultCallbackUrlResolver implements CallbackUrlResolver {

    @Override
    public String compute(final String callbackUrl, WebContext context) {
        return callbackUrl;
    }
}

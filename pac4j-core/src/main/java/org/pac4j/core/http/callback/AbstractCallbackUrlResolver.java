package org.pac4j.core.http.callback;

import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;

/**
 * Abstract callback URL resolver.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public abstract class AbstractCallbackUrlResolver implements CallbackUrlResolver {

    private UrlResolver urlResolver = new DefaultUrlResolver();

    @Override
    public UrlResolver getUrlResolver() {
        return urlResolver;
    }

    public void setUrlResolver(final UrlResolver urlResolver) {
        CommonHelper.assertNotNull("urlResolver", urlResolver);
        this.urlResolver = urlResolver;
    }
}

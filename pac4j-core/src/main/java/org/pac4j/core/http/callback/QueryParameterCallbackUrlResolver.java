package org.pac4j.core.http.callback;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;

/**
 * The client name is added as a query parameter to the callback URL.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class QueryParameterCallbackUrlResolver implements CallbackUrlResolver {

    private String clientNameParameter = Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

    @Override
    public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
        String newUrl = urlResolver.compute(url, context);
        if (newUrl != null && !newUrl.contains(this.clientNameParameter + "=")) {
            newUrl = CommonHelper.addParameter(newUrl, this.clientNameParameter, clientName);
        }
        return newUrl;
    }

    @Override
    public boolean matches(final String clientName, final WebContext context) {
        final String name = context.getRequestParameter(this.clientNameParameter);
        return CommonHelper.areEqualsIgnoreCaseAndTrim(name, clientName);
    }

    public String getClientNameParameter() {
        return clientNameParameter;
    }

    public void setClientNameParameter(final String clientNameParameter) {
        this.clientNameParameter = clientNameParameter;
    }
}

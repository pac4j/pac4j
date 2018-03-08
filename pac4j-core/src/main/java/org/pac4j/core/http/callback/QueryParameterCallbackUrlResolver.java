package org.pac4j.core.http.callback;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * The client name is added as a query parameter to the callback URL.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class QueryParameterCallbackUrlResolver implements CallbackUrlResolver {
    private String clientNameParameter = Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

    private Map<String, String> customParams = new HashMap<>();

    public QueryParameterCallbackUrlResolver() {
    }

    public QueryParameterCallbackUrlResolver(final Map<String, String> customParams) {
        this.customParams = customParams;
    }

    @Override
    public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
        String newUrl = urlResolver.compute(url, context);
        if (newUrl != null && !newUrl.contains(this.clientNameParameter + '=')) {
            newUrl = CommonHelper.addParameter(newUrl, this.clientNameParameter, clientName);
        }
        for (final Map.Entry<String, String> entry : this.customParams.entrySet()) {
            newUrl = CommonHelper.addParameter(newUrl, entry.getKey(), entry.getValue());
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

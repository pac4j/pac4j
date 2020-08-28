package org.pac4j.core.http.callback;

import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Map;

/**
 * The client name is added as a query parameter to the callback URL.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class QueryParameterCallbackUrlResolver extends BaseCallbackUrlResolver {
    private String clientNameParameter = Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

    public QueryParameterCallbackUrlResolver() {
    }

    public QueryParameterCallbackUrlResolver(final BaseClientConfiguration config) {
        super(config);
    }

    public QueryParameterCallbackUrlResolver(final Map<String, String> customParams) {
        super(customParams);
    }

    @Override
    public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
        String newUrl = urlResolver.compute(url, context);
        if (newUrl != null && !newUrl.contains(this.clientNameParameter + '=')) {
            newUrl = CommonHelper.addParameter(newUrl, this.clientNameParameter, clientName);
        }
        return computeUrlCustomParams(newUrl);
    }

    @Override
    public boolean matches(final String clientName, final WebContext context) {
        final String name = context.getRequestParameter(this.clientNameParameter).orElse(null);
        return CommonHelper.areEqualsIgnoreCaseAndTrim(name, clientName);
    }

    public String getClientNameParameter() {
        return clientNameParameter;
    }

    public void setClientNameParameter(final String clientNameParameter) {
        this.clientNameParameter = clientNameParameter;
    }
}

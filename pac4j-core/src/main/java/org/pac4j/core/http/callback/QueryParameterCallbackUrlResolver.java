package org.pac4j.core.http.callback;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * The client name is added as a query parameter to the callback URL.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class QueryParameterCallbackUrlResolver implements CallbackUrlResolver {
    @Getter
    @Setter
    private String clientNameParameter = Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

    private Map<String, String> customParams = new HashMap<>();

    /**
     * <p>Constructor for QueryParameterCallbackUrlResolver.</p>
     */
    public QueryParameterCallbackUrlResolver() {
    }

    /**
     * <p>Constructor for QueryParameterCallbackUrlResolver.</p>
     *
     * @param customParams a {@link Map} object
     */
    public QueryParameterCallbackUrlResolver(final Map<String, String> customParams) {
        this.customParams = customParams;
    }

    /** {@inheritDoc} */
    @Override
    public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
        var newUrl = urlResolver.compute(url, context);
        if (newUrl != null && !newUrl.contains(this.clientNameParameter + '=')) {
            newUrl = CommonHelper.addParameter(newUrl, this.clientNameParameter, clientName);
        }
        for (val entry : this.customParams.entrySet()) {
            newUrl = CommonHelper.addParameter(newUrl, entry.getKey(), entry.getValue());
        }
        return newUrl;
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(final String clientName, final WebContext context) {
        val name = context.getRequestParameter(this.clientNameParameter).orElse(null);
        return CommonHelper.areEqualsIgnoreCaseAndTrim(name, clientName);
    }
}

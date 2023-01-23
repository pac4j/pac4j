package org.pac4j.core.matching.matcher;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Define how the CORS requests are authorized.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@Setter
@ToString
public class CorsMatcher implements Matcher {

    private String allowOrigin;

    private String exposeHeaders;

    private int maxAge = -1;

    private Boolean allowCredentials;

    private Set<HttpConstants.HTTP_METHOD> allowMethods;

    private String allowHeaders;

    @Override
    public boolean matches(final CallContext ctx) {
        val webContext = ctx.webContext();

        CommonHelper.assertNotBlank("allowOrigin", allowOrigin);

        webContext.setResponseHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, allowOrigin);

        if (CommonHelper.isNotBlank(exposeHeaders)) {
            webContext.setResponseHeader(HttpConstants.ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, exposeHeaders);
        }

        if (maxAge != -1) {
            webContext.setResponseHeader(HttpConstants.ACCESS_CONTROL_MAX_AGE_HEADER, Pac4jConstants.EMPTY_STRING + maxAge);
        }

        if (allowCredentials != null && allowCredentials) {
            webContext.setResponseHeader(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, allowCredentials.toString());
        }

        if (allowMethods != null) {
            val methods = allowMethods.stream().map(Enum::toString).collect(Collectors.joining(", "));
            webContext.setResponseHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS_HEADER, methods);
        }

        if (CommonHelper.isNotBlank(allowHeaders)) {
            webContext.setResponseHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS_HEADER, allowHeaders);
        }

        return true;
    }
}

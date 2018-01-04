package org.pac4j.core.authorization.authorizer.csrf;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;
import java.util.Optional;

/**
 * Authorizer that checks CSRF tokens.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class CsrfAuthorizer implements Authorizer<CommonProfile> {

    private String parameterName = Pac4jConstants.CSRF_TOKEN;

    private String headerName = Pac4jConstants.CSRF_TOKEN;

    private boolean onlyCheckPostRequest = true;

    public CsrfAuthorizer() {
    }

    public CsrfAuthorizer(final String parameterName, final String headerName) {
        this.parameterName = parameterName;
        this.headerName = headerName;
    }

    public CsrfAuthorizer(final String parameterName, final String headerName, final boolean onlyCheckPostRequest) {
        this(parameterName, headerName);
        this.onlyCheckPostRequest = onlyCheckPostRequest;
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<CommonProfile> profiles) {
        final boolean checkRequest = !onlyCheckPostRequest || ContextHelper.isPost(context);
        if (checkRequest) {
            final Optional<String> parameterToken = context.getRequestParameter(parameterName);
            final Optional<String> headerToken = context.getRequestHeader(headerName);
            final Optional<String> sessionToken = context.getSessionStore().get(context, Pac4jConstants.CSRF_TOKEN);
            return sessionToken.map(
                sessionTokenValue ->
                    parameterToken.map( pt -> sessionToken.equals(pt)).orElse(false)
                        || headerToken.map( ht -> sessionToken.equals(ht)).orElse(false)

            ).orElse(false);
        } else {
            return true;
        }
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public boolean isOnlyCheckPostRequest() {
        return onlyCheckPostRequest;
    }

    public void setOnlyCheckPostRequest(boolean onlyCheckPostRequest) {
        this.onlyCheckPostRequest = onlyCheckPostRequest;
    }
}

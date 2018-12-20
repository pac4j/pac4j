package org.pac4j.core.authorization.authorizer.csrf;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * Authorizer that checks CSRF tokens.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class CsrfAuthorizer implements Authorizer<UserProfile> {

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
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
        final boolean checkRequest = !onlyCheckPostRequest || ContextHelper.isPost(context);
        if (checkRequest) {
            final String parameterToken = context.getRequestParameter(parameterName);
            final String headerToken = context.getRequestHeader(headerName);
            final String sessionToken = (String) context.getSessionStore().get(context, Pac4jConstants.CSRF_TOKEN);
            return sessionToken != null && (sessionToken.equals(parameterToken) || sessionToken.equals(headerToken));
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

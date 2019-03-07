package org.pac4j.core.authorization.authorizer.csrf;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Optional;

import static org.pac4j.core.context.ContextHelper.*;

/**
 * Authorizer that checks CSRF tokens.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class CsrfAuthorizer implements Authorizer<UserProfile> {

    private String parameterName = Pac4jConstants.CSRF_TOKEN;

    private String headerName = Pac4jConstants.CSRF_TOKEN;

    private boolean checkAllRequests = false;

    public CsrfAuthorizer() {
    }

    public CsrfAuthorizer(final String parameterName, final String headerName) {
        this.parameterName = parameterName;
        this.headerName = headerName;
    }

    public CsrfAuthorizer(final String parameterName, final String headerName, final boolean checkAllRequests) {
        this(parameterName, headerName);
        this.checkAllRequests = checkAllRequests;
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
        final boolean checkRequest = checkAllRequests || isPost(context) || isPut(context) || isPatch(context) || isDelete(context);
        if (checkRequest) {
            final String parameterToken = context.getRequestParameter(parameterName).orElse(null);
            final String headerToken = context.getRequestHeader(headerName).orElse(null);
            final Optional<String> sessionToken = (Optional<String>) context.getSessionStore().get(context, Pac4jConstants.CSRF_TOKEN);
            return sessionToken.isPresent() && (sessionToken.get().equals(parameterToken) || sessionToken.get().equals(headerToken));
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

    public boolean isCheckAllRequests() {
        return checkAllRequests;
    }

    public void setCheckAllRequests(final boolean checkAllRequests) {
        this.checkAllRequests = checkAllRequests;
    }
}

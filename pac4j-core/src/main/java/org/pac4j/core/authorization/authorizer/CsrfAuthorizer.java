package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static org.pac4j.core.context.ContextHelper.*;

/**
 * Authorizer that checks CSRF tokens.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class CsrfAuthorizer implements Authorizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrfAuthorizer.class);

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
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) {
        final var checkRequest = checkAllRequests || isPost(context) || isPut(context) || isPatch(context) || isDelete(context);
        if (checkRequest) {
            final var parameterToken = context.getRequestParameter(parameterName).orElse(null);
            final var headerToken = context.getRequestHeader(headerName).orElse(null);
            LOGGER.debug("parameterToken: {}", parameterToken);
            LOGGER.debug("headerToken: {}", headerToken);
            final var sessionPreviousToken = sessionStore.get(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN);
            final var sessionToken = sessionStore.get(context, Pac4jConstants.CSRF_TOKEN);
            final var sessionDate = sessionStore.get(context, Pac4jConstants.CSRF_TOKEN_EXPIRATION_DATE);
            if (sessionStore.getSessionId(context, false).isPresent()) {
                sessionStore.set(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN, null);
            }
            // all checks are always performed, conditional operations are turned into logical ones,
            // string comparisons are replaced by hash equalities to be protected against time-based attacks
            final var hasSessionData = sessionToken.isPresent() & sessionDate.isPresent();
            final var previousToken = (String) sessionPreviousToken.orElse("");
            LOGGER.debug("previous token: {}", previousToken);
            final var token = (String) sessionToken.orElse("");
            LOGGER.debug("token: {}", token);
            final var isGoodCurrentToken = hashEquals(token, parameterToken) | hashEquals(token, headerToken);
            final var isGoodPreviousToken = hashEquals(previousToken, parameterToken) | hashEquals(previousToken, headerToken);
            final var isGoodToken = isGoodCurrentToken | isGoodPreviousToken;
            final var expirationDate = (Long) sessionDate.orElse(0L);
            final var now = new Date().getTime();
            final var isDateExpired = expirationDate < now;
            if (!hasSessionData | !isGoodToken | isDateExpired) {
                return false;
            }

        }
        return true;
    }

    protected boolean hashEquals(final String a, final String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.hashCode() == b.hashCode();
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

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "parameterName", parameterName, "headerName", headerName,
            "checkAllRequests", checkAllRequests);
    }
}

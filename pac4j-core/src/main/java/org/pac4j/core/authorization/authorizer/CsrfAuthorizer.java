package org.pac4j.core.authorization.authorizer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Date;
import java.util.List;

import static org.pac4j.core.context.WebContextHelper.*;

/**
 * Authorizer that checks CSRF tokens.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Getter
@Setter
@ToString
@Slf4j
public class CsrfAuthorizer implements Authorizer {

    private String parameterName = Pac4jConstants.CSRF_TOKEN;

    private String headerName = Pac4jConstants.CSRF_TOKEN;

    private boolean checkAllRequests = false;

    /**
     * <p>Constructor for CsrfAuthorizer.</p>
     */
    public CsrfAuthorizer() {
    }

    /**
     * <p>Constructor for CsrfAuthorizer.</p>
     *
     * @param parameterName a {@link java.lang.String} object
     * @param headerName a {@link java.lang.String} object
     */
    public CsrfAuthorizer(final String parameterName, final String headerName) {
        this.parameterName = parameterName;
        this.headerName = headerName;
    }

    /**
     * <p>Constructor for CsrfAuthorizer.</p>
     *
     * @param parameterName a {@link java.lang.String} object
     * @param headerName a {@link java.lang.String} object
     * @param checkAllRequests a boolean
     */
    public CsrfAuthorizer(final String parameterName, final String headerName, final boolean checkAllRequests) {
        this(parameterName, headerName);
        this.checkAllRequests = checkAllRequests;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) {
        val checkRequest = checkAllRequests || isPost(context) || isPut(context) || isPatch(context) || isDelete(context);
        if (checkRequest) {
            val parameterToken = context.getRequestParameter(parameterName).orElse(null);
            val headerToken = context.getRequestHeader(headerName).orElse(null);
            LOGGER.debug("parameterToken: {}", parameterToken);
            LOGGER.debug("headerToken: {}", headerToken);
            val sessionPreviousToken = sessionStore.get(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN);
            val sessionToken = sessionStore.get(context, Pac4jConstants.CSRF_TOKEN);
            val sessionDate = sessionStore.get(context, Pac4jConstants.CSRF_TOKEN_EXPIRATION_DATE);
            if (sessionStore.getSessionId(context, false).isPresent()) {
                sessionStore.set(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN, null);
            }
            // all checks are always performed, conditional operations are turned into logical ones,
            // string comparisons are replaced by hash equalities to be protected against time-based attacks
            val hasSessionData = sessionToken.isPresent() & sessionDate.isPresent();
            val previousToken = (String) sessionPreviousToken.orElse(Pac4jConstants.EMPTY_STRING);
            LOGGER.debug("previous token: {}", previousToken);
            val token = (String) sessionToken.orElse(Pac4jConstants.EMPTY_STRING);
            LOGGER.debug("token: {}", token);
            val isGoodCurrentToken = hashEquals(token, parameterToken) | hashEquals(token, headerToken);
            val isGoodPreviousToken = hashEquals(previousToken, parameterToken) | hashEquals(previousToken, headerToken);
            val isGoodToken = isGoodCurrentToken | isGoodPreviousToken;
            val expirationDate = (Long) sessionDate.orElse(0L);
            val now = new Date().getTime();
            val isDateExpired = expirationDate < now;
            if (!hasSessionData | !isGoodToken | isDateExpired) {
                return false;
            }

        }
        return true;
    }

    /**
     * <p>hashEquals.</p>
     *
     * @param a a {@link java.lang.String} object
     * @param b a {@link java.lang.String} object
     * @return a boolean
     */
    protected boolean hashEquals(final String a, final String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.hashCode() == b.hashCode();
    }
}

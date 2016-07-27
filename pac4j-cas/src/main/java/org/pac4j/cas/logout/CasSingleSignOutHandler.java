package org.pac4j.cas.logout;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.session.HashMapBackedSessionMappingStorage;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.session.SingleSignOutHandler;
import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.context.J2EContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the logout handler for the {@link CasClient} class based on the {@link SingleSignOutHandler} class of the Apereo CAS client.</p>
 * <p>It should only be used in J2E context.</p>
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasSingleSignOutHandler extends CasLogoutHandler<J2EContext> {
    
    protected static final Logger logger = LoggerFactory.getLogger(CasSingleSignOutHandler.class);

    private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();

    private String artifactParameterName = Protocol.CAS2.getArtifactParameterName();

    private String logoutParameterName = ConfigurationKeys.LOGOUT_PARAMETER_NAME.getDefaultValue();

    private List<String> safeParameters= Arrays.asList(this.logoutParameterName);
    
    private boolean eagerlyCreateSessions = true;

    private LogoutStrategy logoutStrategy = isServlet30() ? new Servlet30LogoutStrategy() : new Servlet25LogoutStrategy();
    
    public CasSingleSignOutHandler() { }

    @Override
    public void recordSession(final J2EContext context, final String ticket) {
        final HttpServletRequest request = context.getRequest();
        final HttpSession session = request.getSession(this.eagerlyCreateSessions);

        if (session == null) {
            logger.debug("No session currently exists (and none created).  Cannot record session information for single sign out.");
            return;
        }

        final String token = CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters);
        logger.debug("Recording session for token {}", token);

        final String sessionId = session.getId();
        try {
            this.sessionMappingStorage.removeBySessionById(sessionId);
        } catch (final Exception e) {
            logger.warn("failed to remove session by id: ", sessionId);
            // ignore if the session is already marked as invalid.  Nothing we can do!
        }
        sessionMappingStorage.addSessionById(token, session);
    }
    
    @Override
    public void destroySession(final J2EContext context, final String sessionId) {
    final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(sessionId);
        final HttpServletRequest request = context.getRequest();
        if (session != null) {
            String sessionID = session.getId();

            logger.debug("Invalidating session [{}] for token [{}]", sessionID, sessionId);

            try {
                session.invalidate();
            } catch (final IllegalStateException e) {
                logger.debug("Error invalidating session.", e);
            }
            this.logoutStrategy.logout(request);
        }
    }

    private static boolean isServlet30() {
        try {
            return HttpServletRequest.class.getMethod("logout") != null;
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Abstracts the ways we can force logout with the Servlet spec.
     */
    private interface LogoutStrategy {

        void logout(HttpServletRequest request);
    }

    private static class Servlet25LogoutStrategy implements LogoutStrategy {

        @Override
        public void logout(final HttpServletRequest request) {
            // nothing additional to do here
        }
    }

    private static class Servlet30LogoutStrategy implements LogoutStrategy {

        public void logout(final HttpServletRequest request) {
            try {
                request.logout();
            } catch (final ServletException e) {
                logger.debug("Error performing request.logout.");
            }
        }
    }

    public SessionMappingStorage getSessionMappingStorage() {
        return sessionMappingStorage;
    }

    public void setSessionMappingStorage(SessionMappingStorage sessionMappingStorage) {
        this.sessionMappingStorage = sessionMappingStorage;
    }
}

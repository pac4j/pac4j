package org.pac4j.cas.logout;

import javax.servlet.http.HttpSession;

import org.jasig.cas.client.session.HashMapBackedSessionMappingStorage;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.session.SingleSignOutHandler;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.context.J2EContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the logout handler for the {@link CasClient}, inspired by the {@link SingleSignOutHandler} of the Apereo CAS client.</p>
 *
 * <p>It is automatically defined for the {@link J2EContext} by the {@link org.pac4j.cas.config.CasConfiguration} when no {@link CasLogoutHandler} is defined.</p>
 *
 * <p>As web sessions are saved in a specific storage, the {@link org.jasig.cas.client.session.SingleSignOutHttpSessionListener} must also be declared to clean this storage when sessions are destroyed (to avoid an out of memory).</p>
 *
 * <p>To use this logout handler, the <code>renewSession</code> option must be disabled on the "callback filter".</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasSingleSignOutHandler implements CasLogoutHandler<J2EContext> {

    protected static final Logger logger = LoggerFactory.getLogger(CasSingleSignOutHandler.class);

    private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();

    private boolean eagerlyCreateSessions = true;

    public CasSingleSignOutHandler() { }

    @Override
    public void recordSession(final J2EContext context, final String ticket) {
        final HttpSession session = context.getRequest().getSession(this.eagerlyCreateSessions);

        if (session == null) {
            logger.debug("No session currently exists (and none created). Cannot record session information for single sign out.");
            return;
        }

        final String sessionId = session.getId();
        try {
            this.sessionMappingStorage.removeBySessionById(sessionId);
        } catch (final Exception e) {
            logger.warn("failed to remove session by id: ", sessionId);
            // ignore if the session is already marked as invalid.  Nothing we can do!
        }
        sessionMappingStorage.addSessionById(ticket, session);
    }

    @Override
    public void destroySessionBack(final J2EContext context, final String ticket) {
        final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(ticket);
        if (session != null) {
            String sessionID = session.getId();

            logger.debug("Invalidating session [{}] for ticket [{}]", sessionID, ticket);

            try {
                session.invalidate();
            } catch (final IllegalStateException e) {
                logger.debug("Error invalidating session", e);
            }
        }
    }

    public boolean isEagerlyCreateSessions() {
        return eagerlyCreateSessions;
    }

    public void setEagerlyCreateSessions(boolean eagerlyCreateSessions) {
        this.eagerlyCreateSessions = eagerlyCreateSessions;
    }

    public SessionMappingStorage getSessionMappingStorage() {
        return sessionMappingStorage;
    }

    public void setSessionMappingStorage(SessionMappingStorage sessionMappingStorage) {
        this.sessionMappingStorage = sessionMappingStorage;
    }
}

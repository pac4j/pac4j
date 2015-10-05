/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
import org.jasig.cas.client.util.XmlUtils;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the logout handler for the {@link CasClient} class based on the {@link SingleSignOutHandler} class of the Jasig CAS client.</p>
 * <p>It should only be used in J2E context.</p>
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasSingleSignOutHandler implements LogoutHandler {
    
    protected static final Logger logger = LoggerFactory.getLogger(CasSingleSignOutHandler.class);

    private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();

    private String artifactParameterName = Protocol.CAS2.getArtifactParameterName();

    private String logoutParameterName = ConfigurationKeys.LOGOUT_PARAMETER_NAME.getDefaultValue();

    private List<String> safeParameters= Arrays.asList(this.logoutParameterName);
    
    private boolean eagerlyCreateSessions = true;

    private LogoutStrategy logoutStrategy = isServlet30() ? new Servlet30LogoutStrategy() : new Servlet25LogoutStrategy();
    
    /**
     * Construct an instance.
     */
    public CasSingleSignOutHandler() {
    }
    
    /**
     * Construct an instance from a given {@link SingleSignOutHandler} class.
     * The provided handler is not taken into account.
     * 
     * @param singleSignOutHandler the real SSOut handler
     */
    @Deprecated
    public CasSingleSignOutHandler(final SingleSignOutHandler singleSignOutHandler) {
        logger.warn("The provided CAS SingleSignOutHandler is not taken into account anymore!");
    }
    
    public boolean isTokenRequest(final WebContext context) {
        final J2EContext j2eContext = (J2EContext) context;
        return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(j2eContext.getRequest(), this.artifactParameterName,
                this.safeParameters));
    }
    
    public boolean isLogoutRequest(final WebContext context) {
        final J2EContext j2eContext = (J2EContext) context;
        HttpServletRequest request = j2eContext.getRequest();
        return "POST".equals(request.getMethod())
                && !isMultipartRequest(request)
                && CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.logoutParameterName,
                        this.safeParameters));
    }
    
    public void recordSession(final WebContext context, final String ticket) {
        final J2EContext j2eContext = (J2EContext) context;
        HttpServletRequest request = j2eContext.getRequest();
        final HttpSession session = request.getSession(this.eagerlyCreateSessions);

        if (session == null) {
            logger.debug("No session currently exists (and none created).  Cannot record session information for single sign out.");
            return;
        }

        final String token = CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters);
        logger.debug("Recording session for token {}", token);

        try {
            this.sessionMappingStorage.removeBySessionById(session.getId());
        } catch (final Exception e) {
            // ignore if the session is already marked as invalid.  Nothing we can do!
        }
        sessionMappingStorage.addSessionById(token, session);
    }
    
    public void destroySession(final WebContext context) {
        final J2EContext j2eContext = (J2EContext) context;
        HttpServletRequest request = j2eContext.getRequest();
        final String logoutMessage = CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters);
        logger.trace("Logout request:\n{}", logoutMessage);

        final String token = XmlUtils.getTextForElement(logoutMessage, "SessionIndex");
        if (CommonUtils.isNotBlank(token)) {
            final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(token);

            if (session != null) {
                String sessionID = session.getId();

                logger.debug("Invalidating session [{}] for token [{}]", sessionID, token);

                try {
                    session.invalidate();
                } catch (final IllegalStateException e) {
                    logger.debug("Error invalidating session.", e);
                }
                this.logoutStrategy.logout(request);
            }
        }
    }

    private boolean isMultipartRequest(final HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart");
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

    private class Servlet25LogoutStrategy implements LogoutStrategy {

        public void logout(final HttpServletRequest request) {
            // nothing additional to do here
        }
    }

    private class Servlet30LogoutStrategy implements LogoutStrategy {

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

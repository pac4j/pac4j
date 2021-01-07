package org.pac4j.core.engine;

import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultSecurityClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.engine.savedrequest.DefaultSavedRequestHandler;
import org.pac4j.core.engine.savedrequest.SavedRequestHandler;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.checker.MatchingChecker;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.HttpActionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default security logic:</p>
 *
 * <p>If the HTTP request matches the <code>matchers</code> configuration (or no <code>matchers</code> are defined),
 * the security is applied. Otherwise, the user is automatically granted access.</p>
 *
 * <p>First, if the user is not authenticated (no profile) and if some clients have been defined in the <code>clients</code> parameter,
 * a login is tried for the direct clients.</p>
 *
 * <p>Then, if the user has profile, authorizations are checked according to the <code>authorizers</code> configuration.
 * If the authorizations are valid, the user is granted access. Otherwise, a 403 error page is displayed.</p>
 *
 * <p>Finally, if the user is not authenticated (no profile), he is redirected to the appropriate identity provider
 * if the first defined client is an indirect one in the <code>clients</code> configuration. Otherwise, a 401 error page is displayed.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultSecurityLogic extends AbstractExceptionAwareLogic implements SecurityLogic {

    public static final DefaultSecurityLogic INSTANCE = new DefaultSecurityLogic();

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSecurityLogic.class);

    private ClientFinder clientFinder = new DefaultSecurityClientFinder();

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    private MatchingChecker matchingChecker = new DefaultMatchingChecker();

    private SavedRequestHandler savedRequestHandler = new DefaultSavedRequestHandler();

    @Override
    public Object perform(final WebContext context, final SessionStore sessionStore, final Config config,
                          final SecurityGrantedAccessAdapter securityGrantedAccessAdapter, final HttpActionAdapter httpActionAdapter,
                          final String clients, final String authorizers, final String matchers, final Object... parameters) {

        LOGGER.debug("=== SECURITY ===");

        HttpAction action;
        try {
            // checks
            assertNotNull("context", context);
            assertNotNull("config", config);
            assertNotNull("httpActionAdapter", httpActionAdapter);
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("authorizationChecker", authorizationChecker);
            assertNotNull("matchingChecker", matchingChecker);
            final Clients configClients = config.getClients();
            assertNotNull("configClients", configClients);

            // logic
            LOGGER.debug("url: {}", context.getFullRequestURL());
            LOGGER.debug("clients: {} | matchers: {}", clients, matchers);
            final List<Client> currentClients = clientFinder.find(configClients, context, clients);
            LOGGER.debug("currentClients: {}", currentClients);

            if (matchingChecker.matches(context, sessionStore, matchers, config.getMatchers(), currentClients)) {

                final ProfileManager manager = getProfileManager(context, sessionStore);
                manager.setConfig(config);
                List<UserProfile> profiles = loadProfiles(manager, context, sessionStore, currentClients);
                LOGGER.debug("Loaded profiles: {}", profiles);

                // no profile and some current clients
                if (isEmpty(profiles) && isNotEmpty(currentClients)) {
                    boolean updated = false;
                    // loop on all clients searching direct ones to perform authentication
                    for (final Client currentClient : currentClients) {
                        if (currentClient instanceof DirectClient) {
                            LOGGER.debug("Performing authentication for direct client: {}", currentClient);

                            final Optional<Credentials> credentials = currentClient.getCredentials(context);
                            LOGGER.debug("credentials: {}", credentials);
                            if (credentials.isPresent()) {
                                final Optional<UserProfile> optProfile = currentClient.getUserProfile(credentials.get(), context);
                                LOGGER.debug("profile: {}", optProfile);
                                if (optProfile.isPresent()) {
                                    final UserProfile profile = optProfile.get();
                                    final DirectClient directClient = (DirectClient) currentClient;
                                    final boolean saveProfileInSession = directClient.getSaveProfileInSession(context, profile);
                                    final boolean multiProfile = directClient.isMultiProfile(context, profile);
                                    LOGGER.debug("saveProfileInSession: {} / multiProfile: {}", saveProfileInSession, multiProfile);
                                    manager.save(saveProfileInSession, profile, multiProfile);
                                    updated = true;
                                    if (!multiProfile) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (updated) {
                        profiles = loadProfiles(manager, context, sessionStore, currentClients);
                        LOGGER.debug("Reloaded profiles: {}", profiles);
                    }
                }

                // we have profile(s) -> check authorizations; otherwise, redirect to identity provider or 401
                if (isNotEmpty(profiles)) {
                    LOGGER.debug("authorizers: {}", authorizers);
                    if (authorizationChecker.isAuthorized(context, sessionStore, profiles,
                                                          authorizers, config.getAuthorizers(), currentClients)) {
                        LOGGER.debug("authenticated and authorized -> grant access");
                        return securityGrantedAccessAdapter.adapt(context, sessionStore, profiles, parameters);
                    } else {
                        LOGGER.debug("forbidden");
                        action = forbidden(context, sessionStore, currentClients, profiles, authorizers);
                    }
                } else {
                    if (startAuthentication(context, sessionStore, currentClients)) {
                        LOGGER.debug("Starting authentication");
                        saveRequestedUrl(context, sessionStore, currentClients, config.getClients().getAjaxRequestResolver());
                        action = redirectToIdentityProvider(context, sessionStore, currentClients);
                    } else {
                        LOGGER.debug("unauthorized");
                        action = unauthorized(context, sessionStore, currentClients);
                    }
                }

            } else {

                LOGGER.debug("no matching for this request -> grant access");
                return securityGrantedAccessAdapter.adapt(context, sessionStore, Collections.emptyList(), parameters);
            }

        } catch (final Exception e) {
            return handleException(e, httpActionAdapter, context);
        }

        return httpActionAdapter.adapt(action, context);
    }

    /**
     * Load the profiles.
     *
     * @param manager the profile manager
     * @param context the web context
     * @param sessionStore the session store
     * @param clients the current clients
     * @return
     */
    protected List<UserProfile> loadProfiles(final ProfileManager manager, final WebContext context, final SessionStore sessionStore,
                                             final List<Client> clients) {
        return manager.getProfiles();
    }

    /**
     * Return a forbidden error.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param currentClients the current clients
     * @param profiles the current profiles
     * @param authorizers the authorizers
     * @return a forbidden error
     */
    protected HttpAction forbidden(final WebContext context, final SessionStore sessionStore, final List<Client> currentClients,
                                   final List<UserProfile> profiles, final String authorizers) {
        return ForbiddenAction.INSTANCE;
    }

    /**
     * Return whether we must start a login process if the first client is an indirect one.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param currentClients the current clients
     * @return whether we must start a login process
     */
    protected boolean startAuthentication(final WebContext context, final SessionStore sessionStore, final List<Client> currentClients) {
        return isNotEmpty(currentClients) && currentClients.get(0) instanceof IndirectClient;
    }

    /**
     * Save the requested url.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param currentClients the current clients
     * @param ajaxRequestResolver the AJAX request resolver
     */
    protected void saveRequestedUrl(final WebContext context, final SessionStore sessionStore, final List<Client> currentClients,
                                    final AjaxRequestResolver ajaxRequestResolver) {
        if (ajaxRequestResolver == null || !ajaxRequestResolver.isAjax(context)) {
            savedRequestHandler.save(context, sessionStore);
        }
    }

    /**
     * Perform a redirection to start the login process of the first indirect client.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param currentClients the current clients
     * @return the performed redirection
     */
    protected HttpAction redirectToIdentityProvider(final WebContext context, final SessionStore sessionStore,
                                                    final List<Client> currentClients) {
        final IndirectClient currentClient = (IndirectClient) currentClients.get(0);
        return currentClient.getRedirectionAction(context).get();
    }

    /**
     * Return an unauthorized error.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param currentClients the current clients
     * @return an unauthorized error
     */
    protected HttpAction unauthorized(final WebContext context, final SessionStore sessionStore, final List<Client> currentClients) {
        return HttpActionHelper.buildUnauthenticatedAction(context);
    }

    public ClientFinder getClientFinder() {
        return clientFinder;
    }

    public void setClientFinder(final ClientFinder clientFinder) {
        this.clientFinder = clientFinder;
    }

    public AuthorizationChecker getAuthorizationChecker() {
        return authorizationChecker;
    }

    public void setAuthorizationChecker(final AuthorizationChecker authorizationChecker) {
        this.authorizationChecker = authorizationChecker;
    }

    public MatchingChecker getMatchingChecker() {
        return matchingChecker;
    }

    public void setMatchingChecker(final MatchingChecker matchingChecker) {
        this.matchingChecker = matchingChecker;
    }

    public SavedRequestHandler getSavedRequestHandler() {
        return savedRequestHandler;
    }

    public void setSavedRequestHandler(final SavedRequestHandler savedRequestHandler) {
        this.savedRequestHandler = savedRequestHandler;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", this.clientFinder, "authorizationChecker", this.authorizationChecker,
            "matchingChecker", this.matchingChecker, "errorUrl", getErrorUrl(), "savedRequestHandler", savedRequestHandler);
    }
}

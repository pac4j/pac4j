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
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.engine.decision.DefaultProfileStorageDecision;
import org.pac4j.core.engine.decision.ProfileStorageDecision;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.matching.RequireAllMatchersChecker;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.MatchingChecker;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;

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
 * <p>Finally, if the user is still not authenticated (no profile), he is redirected to the appropriate identity provider
 * if the first defined client is an indirect one in the <code>clients</code> configuration. Otherwise, a 401 error page is displayed.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultSecurityLogic<R, C extends WebContext> extends AbstractExceptionAwareLogic<R, C> implements SecurityLogic<R, C> {

    private ClientFinder clientFinder = new DefaultSecurityClientFinder();

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    private MatchingChecker matchingChecker = new RequireAllMatchersChecker();

    private ProfileStorageDecision profileStorageDecision = new DefaultProfileStorageDecision();

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    @Override
    public R perform(final C context, final Config config, final SecurityGrantedAccessAdapter<R, C> securityGrantedAccessAdapter,
                     final HttpActionAdapter<R, C> httpActionAdapter,
                     final String clients, final String authorizers, final String matchers, final Boolean inputMultiProfile,
                     final Object... parameters) {

        logger.debug("=== SECURITY ===");

        HttpAction action;
        try {

            // default value
            final boolean multiProfile;
            if (inputMultiProfile == null) {
                multiProfile = false;
            } else {
                multiProfile = inputMultiProfile;
            }

            // checks
            assertNotNull("context", context);
            assertNotNull("config", config);
            assertNotNull("httpActionAdapter", httpActionAdapter);
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("authorizationChecker", authorizationChecker);
            assertNotNull("matchingChecker", matchingChecker);
            assertNotNull("profileStorageDecision", profileStorageDecision);
            final Clients configClients = config.getClients();
            assertNotNull("configClients", configClients);

            // logic
            logger.debug("url: {}", context.getFullRequestURL());
            logger.debug("matchers: {}", matchers);
            if (matchingChecker.matches(context, matchers, config.getMatchers())) {

                logger.debug("clients: {}", clients);
                final List<Client> currentClients = clientFinder.find(configClients, context, clients);
                logger.debug("currentClients: {}", currentClients);

                final boolean loadProfilesFromSession = profileStorageDecision.mustLoadProfilesFromSession(context, currentClients);
                logger.debug("loadProfilesFromSession: {}", loadProfilesFromSession);
                final ProfileManager manager = getProfileManager(context);
                List<UserProfile> profiles = manager.getAll(loadProfilesFromSession);
                logger.debug("profiles: {}", profiles);

                // no profile and some current clients
                if (isEmpty(profiles) && isNotEmpty(currentClients)) {
                    boolean updated = false;
                    // loop on all clients searching direct ones to perform authentication
                    for (final Client currentClient : currentClients) {
                        if (currentClient instanceof DirectClient) {
                            logger.debug("Performing authentication for direct client: {}", currentClient);

                            final Credentials credentials = currentClient.getCredentials(context);
                            logger.debug("credentials: {}", credentials);
                            final UserProfile profile = currentClient.getUserProfile(credentials, context);
                            logger.debug("profile: {}", profile);
                            if (profile != null) {
                                final boolean saveProfileInSession = profileStorageDecision.mustSaveProfileInSession(context,
                                    currentClients, (DirectClient) currentClient, profile);
                                logger.debug("saveProfileInSession: {} / multiProfile: {}", saveProfileInSession, multiProfile);
                                manager.save(saveProfileInSession, profile, multiProfile);
                                updated = true;
                                if (!multiProfile) {
                                    break;
                                }
                            }
                        }
                    }
                    if (updated) {
                        profiles = manager.getAll(loadProfilesFromSession);
                        logger.debug("new profiles: {}", profiles);
                    }
                }

                // we have profile(s) -> check authorizations
                if (isNotEmpty(profiles)) {
                    logger.debug("authorizers: {}", authorizers);
                    if (authorizationChecker.isAuthorized(context, profiles, authorizers, config.getAuthorizers())) {
                        logger.debug("authenticated and authorized -> grant access");
                        return securityGrantedAccessAdapter.adapt(context, profiles, parameters);
                    } else {
                        logger.debug("forbidden");
                        action = forbidden(context, currentClients, profiles, authorizers);
                    }
                } else {
                    if (startAuthentication(context, currentClients)) {
                        logger.debug("Starting authentication");
                        saveRequestedUrl(context, currentClients);
                        action = redirectToIdentityProvider(context, currentClients);
                    } else {
                        logger.debug("unauthorized");
                        action = unauthorized(context, currentClients);
                    }
                }

            } else {

                logger.debug("no matching for this request -> grant access");
                return securityGrantedAccessAdapter.adapt(context, Arrays.asList(), parameters);
            }

        } catch (final Exception e) {
            return handleException(e, httpActionAdapter, context);
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    /**
     * Return a forbidden error.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @param profiles the current profiles
     * @param authorizers the authorizers
     * @return a forbidden error
     */
    protected HttpAction forbidden(final C context, final List<Client> currentClients, final List<UserProfile> profiles,
                                   final String authorizers) {
        return HttpAction.forbidden(context);
    }

    /**
     * Return whether we must start a login process if the first client is an indirect one.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return whether we must start a login process
     */
    protected boolean startAuthentication(final C context, final List<Client> currentClients) {
        return isNotEmpty(currentClients) && currentClients.get(0) instanceof IndirectClient;
    }

    /**
     * Save the requested url.
     *
     * @param context the web context
     * @param currentClients the current clients
     */
    protected void saveRequestedUrl(final C context, final List<Client> currentClients) {
        if (ajaxRequestResolver == null || !ajaxRequestResolver.isAjax(context)) {
            final String requestedUrl = context.getFullRequestURL();
            logger.debug("requestedUrl: {}", requestedUrl);
            context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, requestedUrl);
        }
    }

    /**
     * Perform a redirection to start the login process of the first indirect client.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return the performed redirection
     */
    protected HttpAction redirectToIdentityProvider(final C context, final List<Client> currentClients) {
        final IndirectClient currentClient = (IndirectClient) currentClients.get(0);
        return currentClient.redirect(context);
    }

    /**
     * Return an unauthorized error.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return an unauthorized error
     */
    protected HttpAction unauthorized(final C context, final List<Client> currentClients) {
        return HttpAction.unauthorized(context);
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

    public ProfileStorageDecision getProfileStorageDecision() {
        return profileStorageDecision;
    }

    public void setProfileStorageDecision(final ProfileStorageDecision profileStorageDecision) {
        this.profileStorageDecision = profileStorageDecision;
    }

    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public void setAjaxRequestResolver(final AjaxRequestResolver ajaxRequestResolver) {
        this.ajaxRequestResolver = ajaxRequestResolver;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", this.clientFinder, "authorizationChecker", this.authorizationChecker,
            "matchingChecker", this.matchingChecker, "profileStorageDecision", this.profileStorageDecision,
            "errorUrl", getErrorUrl(), "ajaxRequestResolver", this.ajaxRequestResolver);
    }
}

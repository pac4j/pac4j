package org.pac4j.core.engine;

import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.matching.DefaultMatchingChecker;
import org.pac4j.core.matching.MatchingChecker;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.ProfileManagerFactoryAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default security logic:</p>
 *
 * <p>If the HTTP request matches the <code>matchers</code> configuration (or no <code>matchers</code> are defined), the security is applied.
 * Otherwise, the user is automatically granted access.</p>
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
public class DefaultSecurityLogic<R, C extends WebContext> extends ProfileManagerFactoryAware<C> implements SecurityLogic<R, C> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private ClientFinder clientFinder = new DefaultClientFinder();

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    private MatchingChecker matchingChecker = new DefaultMatchingChecker();

    private boolean saveProfileInSession;

    @Override
    public R perform(final C context, final Config config, final SecurityGrantedAccessAdapter<R, C> securityGrantedAccessAdapter, final HttpActionAdapter<R, C> httpActionAdapter,
                     final String clients, final String authorizers, final String matchers, final Boolean inputMultiProfile, final Object... parameters) {

        logger.debug("=== SECURITY ===");

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
        final Clients configClients = config.getClients();
        assertNotNull("configClients", configClients);

        // logic
        HttpAction action;
        try {

            logger.debug("url: {}", context.getFullRequestURL());
            logger.debug("matchers: {}", matchers);
            if (matchingChecker.matches(context, matchers, config.getMatchers())) {

                logger.debug("clients: {}", clients);
                final List<Client> currentClients = clientFinder.find(configClients, context, clients);
                logger.debug("currentClients: {}", currentClients);

                final boolean loadProfilesFromSession = loadProfilesFromSession(context, currentClients);
                logger.debug("loadProfilesFromSession: {}", loadProfilesFromSession);
                final ProfileManager manager = getProfileManager(context, config);
                List<CommonProfile> profiles = manager.getAll(loadProfilesFromSession);
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
                            final CommonProfile profile = currentClient.getUserProfile(credentials, context);
                            logger.debug("profile: {}", profile);
                            if (profile != null) {
                                final boolean saveProfileInSession = saveProfileInSession(context, currentClients, (DirectClient) currentClient, profile);
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
                        return securityGrantedAccessAdapter.adapt(context, parameters);
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
                return securityGrantedAccessAdapter.adapt(context, parameters);
            }

        } catch (final HttpAction e) {
            logger.debug("extra HTTP action required in security: {}", e.getCode());
            action = e;
        } catch (final TechnicalException e) {
            throw e;
        } catch (final Throwable e) {
            throw new TechnicalException(e);
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    /**
     * Load the profiles from the web context if no clients are defined or if the first client is an indirect one or the {@link AnonymousClient}.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return whether the profiles must be loaded from the web session
     */
    protected boolean loadProfilesFromSession(final C context, final List<Client> currentClients) {
        return isEmpty(currentClients) || currentClients.get(0) instanceof IndirectClient || currentClients.get(0) instanceof AnonymousClient;
    }

    /**
     * Whether we need to save the profile in session after the authentication of direct client(s). <code>false</code> by default as direct clients profiles
     * are not meant to be saved in the web session.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @param directClient the direct clients
     * @param profile the retrieved profile after login
     * @return whether we need to save the profile in session
     */
    protected boolean saveProfileInSession(final C context, final List<Client> currentClients, final DirectClient directClient, final CommonProfile profile) {
        return this.saveProfileInSession;
    }

    /**
     * Return a forbidden error.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @param profiles the current profiles
     * @param authorizers the authorizers
     * @return a forbidden error
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected HttpAction forbidden(final C context, final List<Client> currentClients, final List<CommonProfile> profiles, final String authorizers) throws HttpAction {
        return HttpAction.forbidden("forbidden", context);
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
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected void saveRequestedUrl(final C context, final List<Client> currentClients) throws HttpAction {
        final String requestedUrl = context.getFullRequestURL();
        logger.debug("requestedUrl: {}", requestedUrl);
        context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, requestedUrl);
    }

    /**
     * Perform a redirection to start the login process of the first indirect client.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return the performed redirection
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected HttpAction redirectToIdentityProvider(final C context, final List<Client> currentClients) throws HttpAction {
        final IndirectClient currentClient = (IndirectClient) currentClients.get(0);
        return currentClient.redirect(context);
    }

    /**
     * Return an unauthorized error.
     *
     * @param context the web context
     * @param currentClients the current clients
     * @return an unauthorized error
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected HttpAction unauthorized(final C context, final List<Client> currentClients) throws HttpAction {
        return HttpAction.unauthorized("unauthorized", context, null);
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

    public boolean isSaveProfileInSession() {
        return saveProfileInSession;
    }

    public void setSaveProfileInSession(final boolean saveProfileInSession) {
        this.saveProfileInSession = saveProfileInSession;
    }
}

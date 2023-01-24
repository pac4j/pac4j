package org.pac4j.core.engine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultSecurityClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.engine.savedrequest.DefaultSavedRequestHandler;
import org.pac4j.core.engine.savedrequest.SavedRequestHandler;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.checker.MatchingChecker;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.HttpActionHelper;

import java.util.Collections;
import java.util.List;

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
@Getter
@Setter
@Slf4j
@ToString(callSuper = true)
@Accessors(chain = true)
public class DefaultSecurityLogic extends AbstractExceptionAwareLogic implements SecurityLogic {

    public static final DefaultSecurityLogic INSTANCE = new DefaultSecurityLogic();

    private ClientFinder clientFinder = new DefaultSecurityClientFinder();

    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    private MatchingChecker matchingChecker = new DefaultMatchingChecker();

    private SavedRequestHandler savedRequestHandler = new DefaultSavedRequestHandler();

    private boolean loadProfilesFromSession = true;

    @Override
    public Object perform(final Config config, final SecurityGrantedAccessAdapter securityGrantedAccessAdapter,
                          final String clients, final String authorizers, final String matchers, final FrameworkParameters parameters) {

        LOGGER.debug("=== SECURITY ===");

        // checks
        val ctx = buildContext(config, parameters);
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();
        val httpActionAdapter = config.getHttpActionAdapter();
        assertNotNull("httpActionAdapter", httpActionAdapter);

        HttpAction action;
        try {
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("authorizationChecker", authorizationChecker);
            assertNotNull("matchingChecker", matchingChecker);

            val configClients = config.getClients();
            assertNotNull("configClients", configClients);

            // logic
            LOGGER.debug("url: {}", webContext.getFullRequestURL());
            LOGGER.debug("clients: {} | matchers: {}", clients, matchers);
            val currentClients = clientFinder.find(configClients, webContext, clients);
            LOGGER.debug("currentClients: {}", currentClients);

            if (matchingChecker.matches(ctx, matchers, config.getMatchers(), currentClients)) {

                val manager = ctx.profileManagerFactory().apply(webContext, sessionStore);
                manager.setConfig(config);
                var profiles = this.loadProfilesFromSession
                    ? loadProfiles(ctx, manager, currentClients)
                    : List.<UserProfile>of();
                LOGGER.debug("Loaded profiles (from session: {}): {} ", this.loadProfilesFromSession, profiles);

                // no profile and some current clients
                if (isEmpty(profiles) && isNotEmpty(currentClients)) {
                    var updated = false;
                    // loop on all clients searching direct ones to perform authentication
                    for (val currentClient : currentClients) {
                        if (currentClient instanceof DirectClient directClient) {
                            LOGGER.debug("Performing authentication for direct client: {}", currentClient);

                            var credentials = currentClient.getCredentials(ctx).orElse(null);
                            credentials = currentClient.validateCredentials(ctx, credentials).orElse(null);
                            LOGGER.debug("credentials: {}", credentials);
                            if (credentials != null && credentials.isForAuthentication()) {
                                val optProfile = currentClient.getUserProfile(ctx, credentials);
                                LOGGER.debug("profile: {}", optProfile);
                                if (optProfile.isPresent()) {
                                    val profile = optProfile.get();
                                    val saveProfileInSession = directClient.getSaveProfileInSession(webContext, profile);
                                    val multiProfile = directClient.isMultiProfile(webContext, profile);
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
                        profiles = loadProfiles(ctx, manager, currentClients);
                        LOGGER.debug("Reloaded profiles: {}", profiles);
                    }
                }

                // we have profile(s) -> check authorizations; otherwise, redirect to identity provider or 401
                if (isNotEmpty(profiles)) {
                    LOGGER.debug("authorizers: {}", authorizers);
                    if (authorizationChecker.isAuthorized(webContext, sessionStore, profiles,
                                                          authorizers, config.getAuthorizers(), currentClients)) {
                        LOGGER.debug("authenticated and authorized -> grant access");
                        return securityGrantedAccessAdapter.adapt(webContext, sessionStore, profiles);
                    } else {
                        LOGGER.debug("forbidden");
                        action = forbidden(ctx, currentClients, profiles, authorizers);
                    }
                } else {
                    if (startAuthentication(ctx, currentClients)) {
                        LOGGER.debug("Starting authentication");
                        saveRequestedUrl(ctx, currentClients, config.getClients().getAjaxRequestResolver());
                        action = redirectToIdentityProvider(ctx, currentClients);
                    } else {
                        LOGGER.debug("unauthorized");
                        action = unauthorized(ctx, currentClients);
                    }
                }

            } else {

                LOGGER.debug("no matching for this request -> grant access");
                return securityGrantedAccessAdapter.adapt(webContext, sessionStore, Collections.emptyList());
            }

        } catch (final Exception e) {
            return handleException(e, httpActionAdapter, webContext);
        }

        return httpActionAdapter.adapt(action, webContext);
    }

    /**
     * Load the profiles.
     *
     * @param ctx the context
     * @param manager the profile manager
     * @param clients the current clients
     * @return
     */
    protected List<UserProfile> loadProfiles(final CallContext ctx, final ProfileManager manager, final List<Client> clients) {
        return manager.getProfiles();
    }

    /**
     * Return a forbidden error.
     *
     * @param ctx the context
     * @param currentClients the current clients
     * @param profiles the current profiles
     * @param authorizers the authorizers
     * @return a forbidden error
     */
    protected HttpAction forbidden(final CallContext ctx, final List<Client> currentClients,
                                   final List<UserProfile> profiles, final String authorizers) {
        return new ForbiddenAction();
    }

    /**
     * Return whether we must start a login process if the first client is an indirect one.
     *
     * @param ctx the context
     * @param currentClients the current clients
     * @return whether we must start a login process
     */
    protected boolean startAuthentication(final CallContext ctx, final List<Client> currentClients) {
        return isNotEmpty(currentClients) && currentClients.get(0) instanceof IndirectClient;
    }

    /**
     * Save the requested url.
     *
     * @param ctx the context
     * @param currentClients the current clients
     * @param ajaxRequestResolver the AJAX request resolver
     */
    protected void saveRequestedUrl(final CallContext ctx, final List<Client> currentClients,
                                    final AjaxRequestResolver ajaxRequestResolver) {
        if (ajaxRequestResolver == null || !ajaxRequestResolver.isAjax(ctx)) {
            savedRequestHandler.save(ctx);
        }
    }

    /**
     * Perform a redirection to start the login process of the first indirect client.
     *
     * @param ctx the context
     * @param currentClients the current clients
     * @return the performed redirection
     */
    protected HttpAction redirectToIdentityProvider(final CallContext ctx, final List<Client> currentClients) {
        val currentClient = (IndirectClient) currentClients.get(0);
        return currentClient.getRedirectionAction(ctx).get();
    }

    /**
     * Return an unauthorized error.
     *
     * @param ctx the context
     * @param currentClients the current clients
     * @return an unauthorized error
     */
    protected HttpAction unauthorized(final CallContext ctx, final List<Client> currentClients) {
        return HttpActionHelper.buildUnauthenticatedAction(ctx.webContext());
    }
}

package org.pac4j.core.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.QueryParameterCallbackUrlResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.logout.NoLogoutActionBuilder;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Indirect client: the requested protected URL is saved, the user is redirected to the identity provider for login and
 * back to the application after the sucessful authentication and finally to the originally requested URL.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@Setter
@Getter
@ToString(callSuper = true)
@Accessors(chain = true)
public abstract class IndirectClient extends BaseClient {

    public static final String ATTEMPTED_AUTHENTICATION_SUFFIX = "$attemptedAuthentication";
    private static final String STATE_SESSION_PARAMETER = "$stateSessionParameter";
    private static final String NONCE_SESSION_PARAMETER = "$nonceSessionParameter";
    private static final String CODE_VERIFIER_SESSION_PARAMETER = "$codeVerifierSessionParameter";

    protected String callbackUrl;

    protected UrlResolver urlResolver;

    protected CallbackUrlResolver callbackUrlResolver;

    private AjaxRequestResolver ajaxRequestResolver;

    private RedirectionActionBuilder redirectionActionBuilder;

    private LogoutActionBuilder logoutActionBuilder = NoLogoutActionBuilder.INSTANCE;

    private boolean checkAuthenticationAttempt = true;

    @Override
    protected void beforeInternalInit(final boolean forceReinit) {
        // check configuration
        assertNotBlank("callbackUrl", this.callbackUrl, "set it up either on this IndirectClient or on the global Config");
        if (this.urlResolver == null) {
            this.urlResolver = new DefaultUrlResolver();
        }
        if (this.callbackUrlResolver == null) {
            this.callbackUrlResolver = newDefaultCallbackUrlResolver();
        }
        if (this.ajaxRequestResolver == null) {
            ajaxRequestResolver = new DefaultAjaxRequestResolver();
        }
        if (saveProfileInSession == null) {
            saveProfileInSession = true;
        }
    }

    @Override
    protected final void afterInternalInit(final boolean forceReinit) {
        // ensures components have been properly initialized
        assertNotNull("redirectionActionBuilder", this.redirectionActionBuilder);
        assertNotNull("credentialsExtractor", getCredentialsExtractor());
        assertNotNull("authenticator", getAuthenticator());
        assertNotNull("profileCreator", getProfileCreator());
        assertNotNull("logoutActionBuilder", this.logoutActionBuilder);
    }

    protected CallbackUrlResolver newDefaultCallbackUrlResolver() {
        return new QueryParameterCallbackUrlResolver();
    }

    /**
     * <p>If an authentication has already been tried for this client and has failed (<code>null</code> credentials) or if the request is
     * an AJAX one, an unauthorized response is thrown instead of a "redirection".</p>
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @return the "redirection" action
     */
    @Override
    public final Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        init();

        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        // it's an AJAX request -> appropriate action
        if (ajaxRequestResolver.isAjax(ctx)) {
            val httpAction = ajaxRequestResolver.buildAjaxResponse(ctx, redirectionActionBuilder);
            logger.debug("AJAX request detected -> returning " + httpAction + " for " + ctx.webContext().getFullRequestURL());
            cleanRequestedUrl(webContext, sessionStore);
            throw httpAction;
        }
        // authentication has already been tried -> unauthorized
        val attemptedAuth = sessionStore.get(webContext, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX);
        if (attemptedAuth.isPresent() && !Pac4jConstants.EMPTY_STRING.equals(attemptedAuth.get())) {
            logger.debug("authentication already attempted -> 401");
            cleanAttemptedAuthentication(webContext, sessionStore);
            cleanRequestedUrl(webContext, sessionStore);
            throw HttpActionHelper.buildUnauthenticatedAction(webContext);
        }

        return redirectionActionBuilder.getRedirectionAction(ctx);
    }

    private void cleanRequestedUrl(final WebContext context, final SessionStore sessionStore) {
        logger.debug("clean requested URL from session");
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, null);
    }

    private void cleanAttemptedAuthentication(final WebContext context, final SessionStore sessionStore) {
        logger.debug("clean authentication attempt from session");
        sessionStore.set(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, null);
    }

    private void saveAttemptedAuthentication(final WebContext context, final SessionStore sessionStore) {
        if (checkAuthenticationAttempt) {
            logger.debug("save authentication attempt in session");
            sessionStore.set(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        }
    }

    /**
     * <p>Get the credentials from the web context. In some cases, a {@link HttpAction} may be thrown:</p>
     * <ul>
     * <li>if the <code>CasClient</code> receives a logout request, it returns a 200 HTTP status code</li>
     * <li>for the <code>IndirectBasicAuthClient</code>, if no credentials are sent to the callback url, an unauthorized response
     * (401 HTTP status code) is returned to request credentials through a popup.</li>
     * </ul>
     *
     * @param ctx the current context
     * @return the credentials
     */
    @Override
    public final Optional<Credentials> getCredentials(final CallContext ctx) {
        init();
        val optCredentials = retrieveCredentials(ctx);
        // no credentials and no profile returned -> save this authentication has already been tried and failed
        if (!optCredentials.isPresent() && getProfileFactoryWhenNotAuthenticated() == null) {
            logger.debug("no credentials and profile returned -> remember the authentication attempt");
            saveAttemptedAuthentication(ctx.webContext(), ctx.sessionStore());
        } else {
            cleanAttemptedAuthentication(ctx.webContext(), ctx.sessionStore());
        }
        return optCredentials;
    }

    @Override
    public final Optional<RedirectionAction> getLogoutAction(final CallContext ctx, final UserProfile currentProfile,
                                                             final String targetUrl) {
        init();
        return logoutActionBuilder.getLogoutAction(ctx, currentProfile, targetUrl);
    }

    public String computeFinalCallbackUrl(final WebContext context) {
        init();
        return callbackUrlResolver.compute(this.urlResolver, this.callbackUrl, this.getName(), context);
    }

    protected void setRedirectionActionBuilderIfUndefined(final RedirectionActionBuilder redirectActionBuilder) {
        if (this.redirectionActionBuilder == null) {
            this.redirectionActionBuilder = redirectActionBuilder;
        }
    }

    protected void setLogoutActionBuilderIfUndefined(final LogoutActionBuilder logoutActionBuilder) {
        if (this.logoutActionBuilder == null || this.logoutActionBuilder == NoLogoutActionBuilder.INSTANCE) {
            this.logoutActionBuilder = logoutActionBuilder;
        }
    }

    public String getStateSessionAttributeName() {
        return getName() + STATE_SESSION_PARAMETER;
    }

    public String getNonceSessionAttributeName() {
        return getName() + NONCE_SESSION_PARAMETER;
    }

    public String getCodeVerifierSessionAttributeName() {
        return getName() + CODE_VERIFIER_SESSION_PARAMETER;
    }
}

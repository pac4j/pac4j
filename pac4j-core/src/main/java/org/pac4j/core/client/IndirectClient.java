package org.pac4j.core.client;

import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
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

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Indirect client: the requested protected URL is saved, the user is redirected to the identity provider for login and
 * back to the application after the sucessful authentication and finally to the originally requested URL.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
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

    @Override
    protected void beforeInternalInit() {
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
    protected final void afterInternalInit() {
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
     * @param context context
     * @return the "redirection" action
     */
    @Override
    public final Optional<RedirectionAction> getRedirectionAction(final WebContext context) {
        init();
        // it's an AJAX request -> appropriate action
        if (ajaxRequestResolver.isAjax(context)) {
            final HttpAction httpAction = ajaxRequestResolver.buildAjaxResponse(context, redirectionActionBuilder);
            logger.debug("AJAX request detected -> returning " + httpAction + " for " + context.getFullRequestURL());
            cleanRequestedUrl(context);
            throw httpAction;
        }
        // authentication has already been tried -> unauthorized
        final Optional<Object> attemptedAuth = context.getSessionStore()
            .get(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX);
        if (attemptedAuth.isPresent() && !"".equals(attemptedAuth.get())) {
            logger.debug("authentication already attempted -> 401");
            cleanAttemptedAuthentication(context);
            cleanRequestedUrl(context);
            throw UnauthorizedAction.INSTANCE;
        }

        return redirectionActionBuilder.getRedirectionAction(context);
    }

    private void cleanRequestedUrl(final WebContext context) {
        logger.debug("clean requested URL");
        context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, "");
    }

    private void cleanAttemptedAuthentication(final WebContext context) {
        logger.debug("clean authentication attempt");
        context.getSessionStore().set(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "");
    }

    /**
     * <p>Get the credentials from the web context. In some cases, a {@link HttpAction} may be thrown:</p>
     * <ul>
     * <li>if the <code>CasClient</code> receives a logout request, it returns a 200 HTTP status code</li>
     * <li>for the <code>IndirectBasicAuthClient</code>, if no credentials are sent to the callback url, an unauthorized response
     * (401 HTTP status code) is returned to request credentials through a popup.</li>
     * </ul>
     *
     * @param context the current web context
     * @return the credentials
     */
    @Override
    public final Optional<Credentials> getCredentials(final WebContext context) {
        init();
        final Optional<Credentials> optCredentials = retrieveCredentials(context);
        // no credentials and no profile returned -> save this authentication has already been tried and failed
        if (!optCredentials.isPresent() && getProfileFactoryWhenNotAuthenticated() == null) {
            logger.debug("no credentials and profile returned -> remember the authentication attempt");
            context.getSessionStore().set(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        } else {
            cleanAttemptedAuthentication(context);
        }
        return optCredentials;
    }

    @Override
    public final Optional<RedirectionAction> getLogoutAction(final WebContext context, final UserProfile currentProfile,
                                                             final String targetUrl) {
        init();
        return logoutActionBuilder.getLogoutAction(context, currentProfile, targetUrl);
    }

    public String computeFinalCallbackUrl(final WebContext context) {
        return callbackUrlResolver.compute(this.urlResolver, this.callbackUrl, this.getName(), context);
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() { return this.callbackUrl; }

    public UrlResolver getUrlResolver() {
        return urlResolver;
    }

    public void setUrlResolver(final UrlResolver urlResolver) {
        this.urlResolver = urlResolver;
    }

    public CallbackUrlResolver getCallbackUrlResolver() {
        return callbackUrlResolver;
    }

    public void setCallbackUrlResolver(final CallbackUrlResolver callbackUrlResolver) {
        this.callbackUrlResolver = callbackUrlResolver;
    }

    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public void setAjaxRequestResolver(final AjaxRequestResolver ajaxRequestResolver) {
        this.ajaxRequestResolver = ajaxRequestResolver;
    }

    public RedirectionActionBuilder getRedirectionActionBuilder() {
        return redirectionActionBuilder;
    }

    protected void defaultRedirectionActionBuilder(final RedirectionActionBuilder redirectActionBuilder) {
        if (this.redirectionActionBuilder == null) {
            this.redirectionActionBuilder = redirectActionBuilder;
        }
    }

    public LogoutActionBuilder getLogoutActionBuilder() {
        return logoutActionBuilder;
    }

    protected void defaultLogoutActionBuilder(final LogoutActionBuilder logoutActionBuilder) {
        if (this.logoutActionBuilder == null || this.logoutActionBuilder == NoLogoutActionBuilder.INSTANCE) {
            this.logoutActionBuilder = logoutActionBuilder;
        }
    }

    public void setRedirectionActionBuilder(final RedirectionActionBuilder redirectionActionBuilder) {
        this.redirectionActionBuilder = redirectionActionBuilder;
    }

    public void setLogoutActionBuilder(final LogoutActionBuilder logoutActionBuilder) {
        this.logoutActionBuilder = logoutActionBuilder;
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

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
                "urlResolver", this.urlResolver, "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver",
                this.ajaxRequestResolver, "redirectionActionBuilder", this.redirectionActionBuilder, "credentialsExtractor",
                getCredentialsExtractor(), "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
                "logoutActionBuilder", this.logoutActionBuilder, "authorizationGenerators", getAuthorizationGenerators());
    }
}

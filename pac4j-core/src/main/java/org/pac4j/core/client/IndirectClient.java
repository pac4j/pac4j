package org.pac4j.core.client;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.QueryParameterCallbackUrlResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.logout.NoLogoutActionBuilder;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;

/**
 * Indirect client: the requested protected URL is saved, the user is redirected to the identity provider for login and
 * back to the application after the sucessful authentication and finally to the originally requested URL.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class IndirectClient<C extends Credentials> extends BaseClient<C> {

    public final static String ATTEMPTED_AUTHENTICATION_SUFFIX = "$attemptedAuthentication";

    protected String callbackUrl;

    protected UrlResolver urlResolver;

    protected CallbackUrlResolver callbackUrlResolver;

    private AjaxRequestResolver ajaxRequestResolver;

    private RedirectActionBuilder redirectActionBuilder;

    private LogoutActionBuilder logoutActionBuilder = NoLogoutActionBuilder.INSTANCE;

    @Override
    protected final void internalInit() {
        // check configuration
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl, "set it up either on this IndirectClient or on the global Config");
        if (this.urlResolver == null) {
            this.urlResolver = new DefaultUrlResolver();
        }
        if (this.callbackUrlResolver == null) {
            this.callbackUrlResolver = newDefaultCallbackUrlResolver();
        }
        if (this.ajaxRequestResolver == null) {
            ajaxRequestResolver = new DefaultAjaxRequestResolver();
        }

        clientInit();

        // ensures components have been properly initialized
        CommonHelper.assertNotNull("redirectActionBuilder", this.redirectActionBuilder);
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());
        CommonHelper.assertNotNull("profileCreator", getProfileCreator());
        CommonHelper.assertNotNull("logoutActionBuilder", this.logoutActionBuilder);
    }

    protected CallbackUrlResolver newDefaultCallbackUrlResolver() {
        return new QueryParameterCallbackUrlResolver();
    }

    /**
     * Initialize the client.
     */
    protected abstract void clientInit();

    @Override
    public final HttpAction redirect(final WebContext context) {
        final RedirectAction action = getRedirectAction(context);
        return action.perform(context);
    }

    /**
     * <p>Get the redirectAction computed for this client. All the logic is encapsulated here. It should not be called be directly, the
     * {@link #redirect(WebContext)} should be generally called instead.</p>
     * <p>If an authentication has already been tried for this client and has failed (<code>null</code> credentials) or if the request is
     * an AJAX one, an authorized response (401 HTTP status code) is returned instead of a redirection.</p>
     *
     * @param context context
     * @return the redirection action
     */
    public RedirectAction getRedirectAction(final WebContext context) {
        init();
        // it's an AJAX request -> appropriate action
        if (ajaxRequestResolver.isAjax(context)) {
            logger.info("AJAX request detected -> returning the appropriate action");
            RedirectAction action = redirectActionBuilder.redirect(context);
            cleanRequestedUrl(context);
            return ajaxRequestResolver.buildAjaxResponse(action.getLocation(), context);
        }
        // authentication has already been tried -> unauthorized
        final String attemptedAuth = (String) context.getSessionStore().get(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX);
        if (CommonHelper.isNotBlank(attemptedAuth)) {
            cleanAttemptedAuthentication(context);
            cleanRequestedUrl(context);
            throw HttpAction.unauthorized(context);
        }

        return redirectActionBuilder.redirect(context);
    }

    private void cleanRequestedUrl(final WebContext context) {
        SessionStore<WebContext> sessionStore = context.getSessionStore();
        if (sessionStore.get(context, Pac4jConstants.REQUESTED_URL) != null) {
            sessionStore.set(context, Pac4jConstants.REQUESTED_URL, "");
        }
    }

    private void cleanAttemptedAuthentication(final WebContext context) {
        SessionStore<WebContext> sessionStore = context.getSessionStore();
        if (sessionStore.get(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX) != null) {
            sessionStore.set(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "");
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
     * @param context the current web context
     * @return the credentials
     */
    @Override
    public final C getCredentials(final WebContext context) {
        init();
        final C credentials = retrieveCredentials(context);
        // no credentials -> save this authentication has already been tried and failed
        if (credentials == null) {
            context.getSessionStore().set(context, getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        } else {
            cleanAttemptedAuthentication(context);
        }
        return credentials;
    }

    @Override
    public final RedirectAction getLogoutAction(final WebContext context, final CommonProfile currentProfile, final String targetUrl) {
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

    public RedirectActionBuilder getRedirectActionBuilder() {
        return redirectActionBuilder;
    }

    protected void defaultRedirectActionBuilder(final RedirectActionBuilder redirectActionBuilder) {
        if (this.redirectActionBuilder == null) {
            this.redirectActionBuilder = redirectActionBuilder;
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

    public void setRedirectActionBuilder(final RedirectActionBuilder redirectActionBuilder) {
        this.redirectActionBuilder = redirectActionBuilder;
    }

    public void setLogoutActionBuilder(final LogoutActionBuilder logoutActionBuilder) {
        this.logoutActionBuilder = logoutActionBuilder;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
                "urlResolver", this.urlResolver, "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver",
                this.ajaxRequestResolver, "redirectActionBuilder", this.redirectActionBuilder, "credentialsExtractor",
                getCredentialsExtractor(), "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
                "logoutActionBuilder", this.logoutActionBuilder, "authorizationGenerators", getAuthorizationGenerators());
    }
}

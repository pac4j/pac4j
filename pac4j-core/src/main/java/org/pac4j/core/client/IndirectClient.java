package org.pac4j.core.client;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.AjaxRequestResolver;
import org.pac4j.core.http.UrlResolver;
import org.pac4j.core.http.DefaultAjaxRequestResolver;
import org.pac4j.core.http.DefaultUrlResolver;
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
public abstract class IndirectClient<C extends Credentials, U extends CommonProfile> extends BaseClient<C, U> {

    public final static String ATTEMPTED_AUTHENTICATION_SUFFIX = "$attemptedAuthentication";

    protected String callbackUrl;

    private boolean includeClientNameInCallbackUrl = true;

    protected UrlResolver urlResolver = new DefaultUrlResolver();

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    private RedirectActionBuilder redirectActionBuilder;

    private LogoutActionBuilder<U> logoutActionBuilder = NoLogoutActionBuilder.INSTANCE;

    @Override
    protected final void internalInit(final WebContext context) {
        // check configuration
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("urlResolver", this.urlResolver);
        CommonHelper.assertNotNull("ajaxRequestResolver", this.ajaxRequestResolver);

        clientInit(context);

        // ensures components have been properly initialized
        CommonHelper.assertNotNull("redirectActionBuilder", this.redirectActionBuilder);
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());
        CommonHelper.assertNotNull("profileCreator", getProfileCreator());
        CommonHelper.assertNotNull("logoutActionBuilder", this.logoutActionBuilder);
    }

    /**
     * Initialize the client.
     *
     * @param context the web context
     */
    protected abstract void clientInit(WebContext context);

    @Override
    public final HttpAction redirect(final WebContext context) throws HttpAction {
        final RedirectAction action = getRedirectAction(context);
        return action.perform(context);
    }

    /**
     * <p>Get the redirectAction computed for this client. All the logic is encapsulated here. It should not be called be directly, the
     * {@link #redirect(WebContext)} should be generally called instead.</p>
     * <p>If an authentication has already been tried for this client and has failed (<code>null</code> credentials) or if the request is an AJAX one,
     * an authorized response (401 HTTP status code) is returned instead of a redirection.</p>
     *
     * @param context context
     * @return the redirection action
     * @throws HttpAction requires an additional HTTP action
     */
    public RedirectAction getRedirectAction(final WebContext context) throws HttpAction {
        init(context);
        // it's an AJAX request -> unauthorized (instead of a redirection)
        if (ajaxRequestResolver.isAjax(context)) {
            logger.info("AJAX request detected -> returning 401");
            cleanRequestedUrl(context);
            throw HttpAction.unauthorized("AJAX request -> 401", context, null);
        }
        // authentication has already been tried -> unauthorized
        final String attemptedAuth = (String) context.getSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX);
        if (CommonHelper.isNotBlank(attemptedAuth)) {
            cleanAttemptedAuthentication(context);
            cleanRequestedUrl(context);
            throw HttpAction.unauthorized("authentication already tried -> forbidden", context, null);
        }

        return redirectActionBuilder.redirect(context);
    }

    private void cleanRequestedUrl(final WebContext context) {
        context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, "");
    }

    private void cleanAttemptedAuthentication(final WebContext context) {
        context.setSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "");
    }

    /**
     * <p>Get the credentials from the web context. In some cases, a {@link HttpAction} may be thrown:</p>
     * <ul>
     * <li>if the <code>CasClient</code> receives a logout request, it returns a 200 HTTP status code</li>
     * <li>for the <code>IndirectBasicAuthClient</code>, if no credentials are sent to the callback url, an unauthorized response (401 HTTP status
     * code) is returned to request credentials through a popup.</li>
     * </ul>
     *
     * @param context the current web context
     * @return the credentials
     * @throws HttpAction whether an additional HTTP action is required
     */
    @Override
    public final C getCredentials(final WebContext context) throws HttpAction {
        init(context);
        final C credentials = retrieveCredentials(context);
        // no credentials -> save this authentication has already been tried and failed
        if (credentials == null) {
            context.setSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        } else {
            cleanAttemptedAuthentication(context);
        }
        return credentials;
    }

    @Override
    public final RedirectAction getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        init(context);
        return logoutActionBuilder.getLogoutAction(context, currentProfile, targetUrl);
    }

    public String computeFinalCallbackUrl(final WebContext context) {
        return urlResolver.compute(callbackUrl, context);
    }

    public boolean isIncludeClientNameInCallbackUrl() {
        return this.includeClientNameInCallbackUrl;
    }

    public void setIncludeClientNameInCallbackUrl(final boolean includeClientNameInCallbackUrl) {
        this.includeClientNameInCallbackUrl = includeClientNameInCallbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() { return this.callbackUrl; }

    /**
     * Use {@link #getUrlResolver()} instead.
     *
     * @return the URL resolver for the callback URL
     */
    @Deprecated
    public UrlResolver getCallbackUrlResolver() {
        return getUrlResolver();
    }

    /**
     * Use {@link #setUrlResolver(UrlResolver)} instead.
     *
     * @param callbackUrlResolver the URL resolver for the callback URL
     */
    @Deprecated
    public void setCallbackUrlResolver(final UrlResolver callbackUrlResolver) {
        setUrlResolver(callbackUrlResolver);
    }

    public UrlResolver getUrlResolver() {
        return urlResolver;
    }

    public void setUrlResolver(final UrlResolver urlResolver) {
        this.urlResolver = urlResolver;
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

    public LogoutActionBuilder<U> getLogoutActionBuilder() {
        return logoutActionBuilder;
    }

    protected void defaultLogoutActionBuilder(final LogoutActionBuilder<U> logoutActionBuilder) {
        if (this.logoutActionBuilder == null || this.logoutActionBuilder == NoLogoutActionBuilder.INSTANCE) {
            this.logoutActionBuilder = logoutActionBuilder;
        }
    }

    public void setRedirectActionBuilder(final RedirectActionBuilder redirectActionBuilder) {
        this.redirectActionBuilder = redirectActionBuilder;
    }

    public void setLogoutActionBuilder(final LogoutActionBuilder<U> logoutActionBuilder) {
        this.logoutActionBuilder = logoutActionBuilder;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
                "urlResolver", this.urlResolver, "ajaxRequestResolver", this.ajaxRequestResolver,
                "includeClientNameInCallbackUrl", this.includeClientNameInCallbackUrl,
                "redirectActionBuilder", this.redirectActionBuilder, "credentialsExtractor", getCredentialsExtractor(),
                "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
                "logoutActionBuilder", this.logoutActionBuilder, "authorizationGenerators", getAuthorizationGenerators());
    }
}

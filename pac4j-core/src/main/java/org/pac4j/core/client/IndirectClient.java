package org.pac4j.core.client;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.AjaxRequestResolver;
import org.pac4j.core.http.CallbackUrlResolver;
import org.pac4j.core.http.DefaultAjaxRequestResolver;
import org.pac4j.core.http.DefaultCallbackUrlResolver;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.logout.NoLogoutActionBuilder;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;

/**
 * Indirect client type using the {@link RedirectActionBuilder}, {@link CredentialsExtractor}, {@link Authenticator},
 * {@link ProfileCreator} and {@link LogoutActionBuilder} concepts.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class IndirectClient<C extends Credentials, U extends CommonProfile> extends BaseClient<C, U> {

    public final static String ATTEMPTED_AUTHENTICATION_SUFFIX = "$attemptedAuthentication";

    protected String callbackUrl;

    private boolean includeClientNameInCallbackUrl = true;

    protected CallbackUrlResolver callbackUrlResolver = new DefaultCallbackUrlResolver();

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    private RedirectActionBuilder redirectActionBuilder;

    private CredentialsExtractor<C> credentialsExtractor;

    private Authenticator<C> authenticator;

    private ProfileCreator<C, U> profileCreator =  AuthenticatorProfileCreator.INSTANCE;

    private LogoutActionBuilder<U> logoutActionBuilder = NoLogoutActionBuilder.INSTANCE;

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("callbackUrlResolver", this.callbackUrlResolver);
        CommonHelper.assertNotNull("ajaxRequestResolver", this.ajaxRequestResolver);
    }

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
    public final RedirectAction getRedirectAction(final WebContext context) throws HttpAction {
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

        CommonHelper.assertNotNull("redirectActionBuilder", this.redirectActionBuilder);

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

    /**
     * Retrieve the credentials.
     *
     * @param context the web context
     * @return the credentials
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected C retrieveCredentials(final WebContext context) throws HttpAction {
        CommonHelper.assertNotNull("credentialsExtractor", this.credentialsExtractor);
        CommonHelper.assertNotNull("authenticator", this.authenticator);

        try {
            final C credentials = this.credentialsExtractor.extract(context);
            if (credentials == null) {
                return null;
            }
            this.authenticator.validate(credentials, context);
            return credentials;
        } catch (CredentialsException e) {
            logger.info("Failed to retrieve or validate credentials: {}", e.getMessage());
            logger.debug("Failed to retrieve or validate credentials", e);

            return null;
        }
    }

    @Override
    protected U retrieveUserProfile(final C credentials, final WebContext context) throws HttpAction {
        CommonHelper.assertNotNull("profileCreator", this.profileCreator);

        final U profile = this.profileCreator.create(credentials, context);
        logger.debug("profile: {}", profile);
        return profile;
    }

    @Override
    public final RedirectAction getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        init(context);

        CommonHelper.assertNotNull("logoutActionBuilder", this.logoutActionBuilder);

        return logoutActionBuilder.getLogoutAction(context, currentProfile, targetUrl);
    }

    public String computeFinalCallbackUrl(final WebContext context) {
        return callbackUrlResolver.compute(callbackUrl, context);
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

    public void setRedirectActionBuilder(final RedirectActionBuilder redirectActionBuilder) {
        if (this.redirectActionBuilder == null) {
            this.redirectActionBuilder = redirectActionBuilder;
        }
    }

    public CredentialsExtractor<C> getCredentialsExtractor() {
        return credentialsExtractor;
    }

    public void setCredentialsExtractor(final CredentialsExtractor<C> credentialsExtractor) {
        if (this.credentialsExtractor == null) {
            this.credentialsExtractor = credentialsExtractor;
        }
    }

    public Authenticator<C> getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(final Authenticator<C> authenticator) {
        if (this.authenticator == null) {
            this.authenticator = authenticator;
        }
    }

    public ProfileCreator<C, U> getProfileCreator() {
        return profileCreator;
    }

    public void setProfileCreator(final ProfileCreator<C, U> profileCreator) {
        if (this.profileCreator == null || this.profileCreator == AuthenticatorProfileCreator.INSTANCE) {
            this.profileCreator = profileCreator;
        }
    }

    public LogoutActionBuilder<U> getLogoutActionBuilder() {
        return logoutActionBuilder;
    }

    public void setLogoutActionBuilder(final LogoutActionBuilder<U> logoutActionBuilder) {
        if (this.logoutActionBuilder == null || this.logoutActionBuilder == NoLogoutActionBuilder.INSTANCE) {
            this.logoutActionBuilder = logoutActionBuilder;
        }
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
                "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver", this.ajaxRequestResolver,
                "includeClientNameInCallbackUrl", this.includeClientNameInCallbackUrl,
                "redirectActionBuilder", this.redirectActionBuilder, "credentialsExtractor", this.credentialsExtractor,
                "authenticator", this.authenticator, "profileCreator", this.profileCreator,
                "logoutActionBuilder", this.logoutActionBuilder);
    }
}

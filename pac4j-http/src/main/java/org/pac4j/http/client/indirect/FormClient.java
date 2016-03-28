package org.pac4j.http.client.indirect;

import org.pac4j.core.client.IndirectClientV2;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

/**
 * <p>This class is the client to authenticate users through HTTP form.</p>
 * <p>The login url of the form must be defined through the {@link #setLoginUrl(String)} method. For authentication, the user is redirected to
 * this login form. The username and password inputs must be posted on the callback url. Their names can be defined by using the
 * {@link #setUsernameParameter(String)} and {@link #setPasswordParameter(String)} methods.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class FormClient extends IndirectClientV2<UsernamePasswordCredentials, CommonProfile> {

    private String loginUrl;

    public final static String ERROR_PARAMETER = "error";

    public final static String MISSING_FIELD_ERROR = "missing_field";

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    public FormClient() {
    }

    public FormClient(final String loginUrl, final Authenticator usernamePasswordAuthenticator) {
        this.loginUrl = loginUrl;
        setAuthenticator(usernamePasswordAuthenticator);
    }

    public FormClient(final String loginUrl, final String usernameParameter, final String passwordParameter,
                      final Authenticator usernamePasswordAuthenticator) {
        this.loginUrl = loginUrl;
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        setAuthenticator(usernamePasswordAuthenticator);
    }

    public FormClient(final String loginUrl, final Authenticator usernamePasswordAuthenticator,
                      final ProfileCreator profileCreator) {
        this.loginUrl = loginUrl;
        setAuthenticator(usernamePasswordAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("loginUrl", this.loginUrl);
        CommonHelper.assertNotBlank("usernameParameter", this.usernameParameter);
        CommonHelper.assertNotBlank("passwordParameter", this.passwordParameter);
        setRedirectActionBuilder(webContext -> RedirectAction.redirect(this.loginUrl));
        setCredentialsExtractor(new FormExtractor(usernameParameter, passwordParameter, getName()));
        super.internalInit(context);
        assertAuthenticatorTypes(UsernamePasswordAuthenticator.class);
    }

    @Override
    protected UsernamePasswordCredentials retrieveCredentials(final WebContext context) throws RequiresHttpAction {
        final String username = context.getRequestParameter(this.usernameParameter);
        UsernamePasswordCredentials credentials;
        try {
            // retrieve credentials
            credentials = getCredentialsExtractor().extract(context);
            logger.debug("usernamePasswordCredentials: {}", credentials);
            if (credentials == null) {
				// it's an AJAX request -> unauthorized (instead of a
				// redirection)
				if (getAjaxRequestResolver().isAjax(context)) {
					logger.info("AJAX request detected -> returning 401");
					throw RequiresHttpAction.unauthorized("AJAX request -> 401", context, null);
				} else {
					String redirectionUrl = CommonHelper.addParameter(this.loginUrl, this.usernameParameter, username);
					redirectionUrl = CommonHelper.addParameter(redirectionUrl, ERROR_PARAMETER, MISSING_FIELD_ERROR);
					logger.debug("redirectionUrl: {}", redirectionUrl);
					final String message = "Username and password cannot be blank -> return to the form with error";
					logger.debug(message);
					throw RequiresHttpAction.redirect(message, context, redirectionUrl);
				}
            }
            // validate credentials
            getAuthenticator().validate(credentials);
        } catch (final CredentialsException e) {
			// it's an AJAX request -> forbidden (instead of a redirection)
			if (getAjaxRequestResolver().isAjax(context)) {
				logger.info("AJAX request detected -> returning 403");
				throw RequiresHttpAction.forbidden("AJAX request -> 403", context);
			} else {
				String redirectionUrl = CommonHelper.addParameter(this.loginUrl, this.usernameParameter, username);
				String errorMessage = computeErrorMessage(e);
				redirectionUrl = CommonHelper.addParameter(redirectionUrl, ERROR_PARAMETER, errorMessage);
				logger.debug("redirectionUrl: {}", redirectionUrl);
				final String message = "Credentials validation fails -> return to the form with error";
				logger.debug(message);
				throw RequiresHttpAction.redirect(message, context, redirectionUrl);
			}
        }

        return credentials;
    }

    /**
     * Return the error message depending on the thrown exception. Can be overriden for other message computation.
     *
     * @param e the technical exception
     * @return the error message
     */
    protected String computeErrorMessage(final TechnicalException e) {
        return e.getClass().getSimpleName();
    }

    public String getLoginUrl() {
        return this.loginUrl;
    }

    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getUsernameParameter() {
        return this.usernameParameter;
    }

    public void setUsernameParameter(final String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return this.passwordParameter;
    }

    public void setPasswordParameter(final String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName(), "loginUrl",
                this.loginUrl, "usernameParameter", this.usernameParameter, "passwordParameter", this.passwordParameter,
                "redirectActionBuilder", getRedirectActionBuilder(), "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
                "profileCreator", getProfileCreator());
    }
}

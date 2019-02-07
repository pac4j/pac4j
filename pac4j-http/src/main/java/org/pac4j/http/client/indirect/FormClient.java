package org.pac4j.http.client.indirect;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import java.util.Optional;

/**
 * <p>This class is the client to authenticate users through HTTP form.</p>
 * <p>The login url of the form must be defined through the {@link #setLoginUrl(String)} method. For authentication, the user is
 * redirected to this login form. The username and password inputs must be posted on the callback url. Their names can be defined by using
 * the {@link #setUsernameParameter(String)} and {@link #setPasswordParameter(String)} methods.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class FormClient extends IndirectClient<UsernamePasswordCredentials> {

    private String loginUrl;

    public final static String ERROR_PARAMETER = "error";

    public final static String MISSING_FIELD_ERROR = "missing_field";

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    public FormClient() {
    }

    public FormClient(final String loginUrl, final Authenticator usernamePasswordAuthenticator) {
        this.loginUrl = loginUrl;
        defaultAuthenticator(usernamePasswordAuthenticator);
    }

    public FormClient(final String loginUrl, final String usernameParameter, final String passwordParameter,
                      final Authenticator usernamePasswordAuthenticator) {
        this.loginUrl = loginUrl;
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        defaultAuthenticator(usernamePasswordAuthenticator);
    }

    public FormClient(final String loginUrl, final Authenticator usernamePasswordAuthenticator,
                      final ProfileCreator profileCreator) {
        this.loginUrl = loginUrl;
        defaultAuthenticator(usernamePasswordAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotBlank("loginUrl", this.loginUrl);
        CommonHelper.assertNotBlank("usernameParameter", this.usernameParameter);
        CommonHelper.assertNotBlank("passwordParameter", this.passwordParameter);

        defaultRedirectionActionBuilder(ctx -> {
            final String finalLoginUrl = getUrlResolver().compute(this.loginUrl, ctx);
            return Optional.of(new FoundAction(finalLoginUrl));
        });
        defaultCredentialsExtractor(new FormExtractor(usernameParameter, passwordParameter));
    }

    @Override
    protected Optional<UsernamePasswordCredentials> retrieveCredentials(final WebContext context) {
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());

        final String username = context.getRequestParameter(this.usernameParameter).orElse(null);
        final Optional<UsernamePasswordCredentials> credentials;
        try {
            // retrieve credentials
            credentials = getCredentialsExtractor().extract(context);
            logger.debug("usernamePasswordCredentials: {}", credentials);
            if (!credentials.isPresent()) {
                throw handleInvalidCredentials(context, username, "Username and password cannot be blank -> return to the form with error",
                    MISSING_FIELD_ERROR);
            }
            // validate credentials
            getAuthenticator().validate(credentials.get(), context);
        } catch (final CredentialsException e) {
            throw handleInvalidCredentials(context, username, "Credentials validation fails -> return to the form with error",
                computeErrorMessage(e));
        }

        return credentials;
    }

    protected HttpAction handleInvalidCredentials(final WebContext context, final String username, String message, String errorMessage) {
        // it's an AJAX request -> unauthorized (instead of a redirection)
        if (getAjaxRequestResolver().isAjax(context)) {
            logger.info("AJAX request detected -> returning 401");
            return UnauthorizedAction.INSTANCE;
        } else {
            String redirectionUrl = CommonHelper.addParameter(this.loginUrl, this.usernameParameter, username);
            redirectionUrl = CommonHelper.addParameter(redirectionUrl, ERROR_PARAMETER, errorMessage);
            logger.debug("redirectionUrl: {}", redirectionUrl);
            return new FoundAction(redirectionUrl);
        }
    }

    /**
     * Return the error message depending on the thrown exception. Can be overriden for other message computation.
     *
     * @param e the technical exception
     * @return the error message
     */
    protected String computeErrorMessage(final Exception e) {
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
        return CommonHelper.toNiceString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName(), "loginUrl",
                this.loginUrl, "usernameParameter", this.usernameParameter, "passwordParameter", this.passwordParameter,
                "redirectionActionBuilder", getRedirectionActionBuilder(), "extractor", getCredentialsExtractor(),
                "authenticator", getAuthenticator(), "profileCreator", getProfileCreator());
    }
}

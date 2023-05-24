package org.pac4j.http.client.indirect;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>This class is the client to authenticate users through HTTP form.</p>
 * <p>The login url of the form must be defined through the <code>setLoginUrl(String)</code> method. For authentication, the user is
 * redirected to this login form. The username and password inputs must be posted on the callback url. Their names can be defined by using
 * the <code>setUsernameParameter(String)</code> and <code>setPasswordParameter(String)</code> methods.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class FormClient extends IndirectClient {

    private String loginUrl;

    /** Constant <code>ERROR_PARAMETER="error"</code> */
    public final static String ERROR_PARAMETER = "error";

    /** Constant <code>MISSING_FIELD_ERROR="missing_field"</code> */
    public final static String MISSING_FIELD_ERROR = "missing_field";

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    /**
     * <p>Constructor for FormClient.</p>
     */
    public FormClient() {
    }

    /**
     * <p>Constructor for FormClient.</p>
     *
     * @param loginUrl a {@link String} object
     * @param usernamePasswordAuthenticator a {@link Authenticator} object
     */
    public FormClient(final String loginUrl, final Authenticator usernamePasswordAuthenticator) {
        this.loginUrl = loginUrl;
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    /**
     * <p>Constructor for FormClient.</p>
     *
     * @param loginUrl a {@link String} object
     * @param usernameParameter a {@link String} object
     * @param passwordParameter a {@link String} object
     * @param usernamePasswordAuthenticator a {@link Authenticator} object
     */
    public FormClient(final String loginUrl, final String usernameParameter, final String passwordParameter,
                      final Authenticator usernamePasswordAuthenticator) {
        this.loginUrl = loginUrl;
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    /**
     * <p>Constructor for FormClient.</p>
     *
     * @param loginUrl a {@link String} object
     * @param usernamePasswordAuthenticator a {@link Authenticator} object
     * @param profileCreator a {@link ProfileCreator} object
     */
    public FormClient(final String loginUrl, final Authenticator usernamePasswordAuthenticator,
                      final ProfileCreator profileCreator) {
        this.loginUrl = loginUrl;
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("loginUrl", this.loginUrl);
        assertNotBlank("usernameParameter", this.usernameParameter);
        assertNotBlank("passwordParameter", this.passwordParameter);

        setRedirectionActionBuilderIfUndefined(ctx -> {
            val webContext = ctx.webContext();
            val finalLoginUrl = getUrlResolver().compute(this.loginUrl, webContext);
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, finalLoginUrl));
        });
        setCredentialsExtractorIfUndefined(new FormExtractor(usernameParameter, passwordParameter));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> getCredentials(final CallContext ctx) {
        init();
        assertNotNull("credentialsExtractor", getCredentialsExtractor());

        val username = ctx.webContext().getRequestParameter(this.usernameParameter).orElse(null);
        final Optional<Credentials> credentials;
        try {
            credentials = getCredentialsExtractor().extract(ctx);
            logger.debug("usernamePasswordCredentials: {}", credentials);
            if (credentials.isEmpty()) {
                throw handleInvalidCredentials(ctx, username,
                    "Username and password cannot be blank -> return to the form with error", MISSING_FIELD_ERROR);
            }
            return credentials;
        } catch (final CredentialsException e) {
            throw handleInvalidCredentials(ctx, username,
                "Credentials validation fails -> return to the form with error", computeErrorMessage(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<Credentials> internalValidateCredentials(final CallContext ctx, final Credentials credentials) {
        assertNotNull("authenticator", getAuthenticator());

        val username = ((UsernamePasswordCredentials) credentials).getUsername();
        try {
            return getAuthenticator().validate(ctx, credentials);
        } catch (final CredentialsException e) {
            throw handleInvalidCredentials(ctx, username,
                "Credentials validation fails -> return to the form with error", computeErrorMessage(e));
        }
    }

    /**
     * <p>handleInvalidCredentials.</p>
     *
     * @param ctx a {@link CallContext} object
     * @param username a {@link String} object
     * @param message a {@link String} object
     * @param errorMessage a {@link String} object
     * @return a {@link HttpAction} object
     */
    protected HttpAction handleInvalidCredentials(final CallContext ctx, final String username, String message, String errorMessage) {
        val webContext = ctx.webContext();

        // it's an AJAX request -> unauthorized (instead of a redirection)
        if (getAjaxRequestResolver().isAjax(ctx)) {
            logger.info("AJAX request detected -> returning 401");
            return HttpActionHelper.buildUnauthenticatedAction(webContext);
        } else {
            var redirectionUrl = addParameter(this.loginUrl, this.usernameParameter, username);
            redirectionUrl = addParameter(redirectionUrl, ERROR_PARAMETER, errorMessage);
            logger.debug("redirectionUrl: {}", redirectionUrl);
            return HttpActionHelper.buildRedirectUrlAction(webContext, redirectionUrl);
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
}

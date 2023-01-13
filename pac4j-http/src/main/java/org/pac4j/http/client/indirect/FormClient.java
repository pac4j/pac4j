package org.pac4j.http.client.indirect;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
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

    public final static String ERROR_PARAMETER = "error";

    public final static String MISSING_FIELD_ERROR = "missing_field";

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    public FormClient() {
    }

    public FormClient(final String loginUrl, final Authenticator usernamePasswordAuthenticator) {
        this.loginUrl = loginUrl;
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    public FormClient(final String loginUrl, final String usernameParameter, final String passwordParameter,
                      final Authenticator usernamePasswordAuthenticator) {
        this.loginUrl = loginUrl;
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    public FormClient(final String loginUrl, final Authenticator usernamePasswordAuthenticator,
                      final ProfileCreator profileCreator) {
        this.loginUrl = loginUrl;
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("loginUrl", this.loginUrl);
        assertNotBlank("usernameParameter", this.usernameParameter);
        assertNotBlank("passwordParameter", this.passwordParameter);

        setRedirectionActionBuilderIfUndefined((ctx, session, profileManagerFactory) -> {
            val finalLoginUrl = getUrlResolver().compute(this.loginUrl, ctx);
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx, finalLoginUrl));
        });
        setCredentialsExtractorIfUndefined(new FormExtractor(usernameParameter, passwordParameter));
    }

    @Override
    protected Optional<Credentials> retrieveCredentials(final WebContext context, final SessionStore sessionStore,
                                                        final ProfileManagerFactory profileManagerFactory) {
        assertNotNull("credentialsExtractor", getCredentialsExtractor());
        assertNotNull("authenticator", getAuthenticator());

        val username = context.getRequestParameter(this.usernameParameter).orElse(null);
        final Optional<Credentials> credentials;
        try {
            // retrieve credentials
            credentials = getCredentialsExtractor().extract(context, sessionStore, profileManagerFactory);
            logger.debug("usernamePasswordCredentials: {}", credentials);
            if (!credentials.isPresent()) {
                throw handleInvalidCredentials(context, sessionStore, username,
                    "Username and password cannot be blank -> return to the form with error", MISSING_FIELD_ERROR);
            }
            // validate credentials
            getAuthenticator().validate(credentials.get(), context, sessionStore);
        } catch (final CredentialsException e) {
            throw handleInvalidCredentials(context, sessionStore, username,
                "Credentials validation fails -> return to the form with error", computeErrorMessage(e));
        }

        return credentials;
    }

    protected HttpAction handleInvalidCredentials(final WebContext context, final SessionStore sessionStore,
                                                  final String username, String message, String errorMessage) {
        // it's an AJAX request -> unauthorized (instead of a redirection)
        if (getAjaxRequestResolver().isAjax(context, sessionStore)) {
            logger.info("AJAX request detected -> returning 401");
            return HttpActionHelper.buildUnauthenticatedAction(context);
        } else {
            var redirectionUrl = addParameter(this.loginUrl, this.usernameParameter, username);
            redirectionUrl = addParameter(redirectionUrl, ERROR_PARAMETER, errorMessage);
            logger.debug("redirectionUrl: {}", redirectionUrl);
            return HttpActionHelper.buildRedirectUrlAction(context, redirectionUrl);
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

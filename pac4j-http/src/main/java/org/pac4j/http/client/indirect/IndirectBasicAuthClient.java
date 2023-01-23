package org.pac4j.http.client.indirect;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.AuthenticationCredentials;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.BasicAuthExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * <p>This class is the client to authenticate users through HTTP basic auth.</p>
 * <p>For authentication, the user is redirected to the callback url. If the user is not authenticated by basic auth, a
 * specific exception : {@link HttpAction} is returned which must be handled by the application to force
 * authentication.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class IndirectBasicAuthClient extends IndirectClient {

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    public IndirectBasicAuthClient() {}

    public IndirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator) {
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    public IndirectBasicAuthClient(final String realmName, final Authenticator usernamePasswordAuthenticator) {
        this.realmName = realmName;
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    public IndirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator, final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("realmName", this.realmName);

        setRedirectionActionBuilderIfUndefined(ctx -> {
            val webContext = ctx.webContext();
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, computeFinalCallbackUrl(webContext)));
        });
        setCredentialsExtractorIfUndefined(new BasicAuthExtractor());
    }

    @Override
    public Optional<Credentials> getCredentials(final CallContext ctx) {
        init();
        assertNotNull("credentialsExtractor", getCredentialsExtractor());

        val webContext = ctx.webContext();
        // set the www-authenticate in case of error
        webContext.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, HttpConstants.BASIC_HEADER_PREFIX + "realm=\"" + realmName + "\"");

        final Optional<Credentials> credentials;
        try {
            credentials = getCredentialsExtractor().extract(ctx);
            logger.debug("credentials : {}", credentials);

            if (!credentials.isPresent()) {
                throw HttpActionHelper.buildUnauthenticatedAction(webContext);
            }

            return credentials;
        } catch (final CredentialsException e) {
            throw HttpActionHelper.buildUnauthenticatedAction(webContext);
        }
    }

    @Override
    public Optional<AuthenticationCredentials> validateCredentials(final CallContext ctx, final AuthenticationCredentials credentials) {
        init();
        assertNotNull("authenticator", getAuthenticator());

        val webContext = ctx.webContext();
        // set the www-authenticate in case of error
        webContext.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, HttpConstants.BASIC_HEADER_PREFIX + "realm=\"" + realmName + "\"");

        try {
            return getAuthenticator().validate(ctx, credentials);
        } catch (final CredentialsException e) {
            throw HttpActionHelper.buildUnauthenticatedAction(webContext);
        }
    }
}

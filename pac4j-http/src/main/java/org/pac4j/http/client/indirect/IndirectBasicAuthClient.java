package org.pac4j.http.client.indirect;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.extractor.BasicAuthExtractor;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>This class is the client to authenticate users through HTTP basic auth. It was previously named: <code>BasicAuthClient</code>.</p>
 * <p>For authentication, the user is redirected to the callback url. If the user is not authenticated by basic auth, a
 * specific exception : {@link HttpAction} is returned which must be handled by the application to force
 * authentication.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IndirectBasicAuthClient extends IndirectClient<UsernamePasswordCredentials> {

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    public IndirectBasicAuthClient() {}

    public IndirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator) {
        defaultAuthenticator(usernamePasswordAuthenticator);
    }

    public IndirectBasicAuthClient(final String realmName, final Authenticator usernamePasswordAuthenticator) {
        this.realmName = realmName;
        defaultAuthenticator(usernamePasswordAuthenticator);
    }

    public IndirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator, final ProfileCreator profileCreator) {
        defaultAuthenticator(usernamePasswordAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit() {
        assertNotBlank("realmName", this.realmName);

        defaultRedirectActionBuilder(webContext ->  RedirectAction.redirect(computeFinalCallbackUrl(webContext)));
        defaultCredentialsExtractor(new BasicAuthExtractor());
    }

    @Override
    protected UsernamePasswordCredentials retrieveCredentials(final WebContext context) {
        assertNotNull("credentialsExtractor", getCredentialsExtractor());
        assertNotNull("authenticator", getAuthenticator());

        // set the www-authenticate in case of error
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Basic realm=\"" + realmName + "\"");

        final UsernamePasswordCredentials credentials;
        try {
            // retrieve credentials
            credentials = getCredentialsExtractor().extract(context);
            logger.debug("credentials : {}", credentials);

            if (credentials == null) {
                throw HttpAction.unauthorized(context);
            }

            // validate credentials
            getAuthenticator().validate(credentials, context);
        } catch (final CredentialsException e) {
            throw HttpAction.unauthorized(context);
        }

        return credentials;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
            "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver", getAjaxRequestResolver(),
            "redirectActionBuilder", getRedirectActionBuilder(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "logoutActionBuilder", getLogoutActionBuilder(), "authorizationGenerators", getAuthorizationGenerators(),
            "realmName", this.realmName);
    }
}

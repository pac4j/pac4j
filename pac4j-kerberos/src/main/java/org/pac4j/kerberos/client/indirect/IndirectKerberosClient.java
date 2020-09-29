package org.pac4j.kerberos.client.indirect;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.core.exception.http.UnauthorizedAction;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;

import java.util.Optional;

/**
 * @author Vidmantas Zemleris, at Kensu.io
 *
 * @since 2.1.0
 */
public class IndirectKerberosClient extends IndirectClient {

    public IndirectKerberosClient() {}

    public IndirectKerberosClient(final Authenticator authenticator) {
        defaultAuthenticator(authenticator);
    }

    public IndirectKerberosClient(final Authenticator authenticator, final ProfileCreator profileCreator) {
        defaultAuthenticator(authenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit() {
        defaultRedirectionActionBuilder(webContext ->
            Optional.of(RedirectionActionHelper.buildRedirectUrlAction(webContext, computeFinalCallbackUrl(webContext))));
        defaultCredentialsExtractor(new KerberosExtractor());
    }

    @Override
    protected Optional<Credentials> retrieveCredentials(final WebContext context) {
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());

        // set the www-authenticate in case of error
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Negotiate");

        final Optional<Credentials> credentials;
        try {
            // retrieve credentials
            credentials = getCredentialsExtractor().extract(context);
            logger.debug("kerberos credentials : {}", credentials);
            if (!credentials.isPresent()) {
                throw UnauthorizedAction.INSTANCE;
            }
            // validate credentials
            getAuthenticator().validate(credentials.get(), context);
        } catch (final CredentialsException e) {
            throw UnauthorizedAction.INSTANCE;
        }

        return credentials;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName(),
            "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
            "profileCreator", getProfileCreator());
    }
}
